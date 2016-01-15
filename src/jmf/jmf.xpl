export jmf, test

import 'src/jmf/ast.xpl'

jmf = { 

  jmf -> class;
  class -> whitespace 'class' n=name m=metaclass ss=supers lcurl as=(a=attribute semi {a})* fs=field* cs=cnstr* ms=method* whitespace rcurl { 
    Class(n,m,ss,as,fs,cs,ms) 
  };
  metaclass -> whitespace 'metaclass' exp | { Var('Class') };
  supers    -> whitespace 'extends' c=exp cs=(comma exp)* { c:cs } | {[Var('Obj')]};
  attribute -> n=name whitespace ':' t=type { Attribute(n,t) };
  field     -> n=name eql e=exp semi { Slot(n,e) };
  method    -> n=name whitespace '(' fs=formals ')' selfSuper=optSelfSuper whitespace ':' t=type whitespace e=('=' e=exp semi {e} | block) { Method(selfSuper,n,fs,t,e) };
  optSelfSuper  -> '[' self=name whitespace ',' super=name whitespace ']' {[self,super]} | {['self','super']};
  cnstr -> n=name whitespace '(' fs=formals ')' e=('=' e=exp semi {e} | block) { Cnstr(n,fs,e) };
  package -> whitespace 'package' n=name lcurl cs=class* rcurl { Package(n,cs) };
  
  file(name)      -> cs=tplvlBind* EOF {cs};
  topLevelCommand -> whitespace d=jmfCommand {d};
  jmfCommand      -> x=tplvlBind ! {x} | x=tplvlSet ! {x} | x=tplvlExp ! {x} | x=tplvlQuit ! {x} | x=tplvlImport ! {x};
  tplvlBind       -> c=class { TplvlBind(c) } | o=headlessOp { TplvlBind(o) } | p=package { TplvlBind(p) };
  tplvlSet        -> n=name ':=' e=exp ';' { TplvlSet(n,e) };
  tplvlQuit       -> 'quit' {'commands.Quit'()};
  tplvlImport     -> 'import' ns=strings ';' {Import(ns)};
  tplvlExp        -> e=exp ';' { TplvlExp(e) }; 
  
  exp               -> a=arithExp optBoolExp^(a);
  optBoolExp(left)  -> whitespace '&&' right=exp { Bin(left,'&&',right) } | eql right=exp { Bin(left,'=',right) } | { left };
  arithExp          -> s=dot optArithExp^(s);
  optArithExp(left) -> '+' right=exp { Bin(left,'+',right) } | { left };
  dot               -> e=apply sendToOrGetFrom^(e);
  sendToOrGetFrom(o)-> sendTo^(o) | getFrom^(o) | {o};
  sendTo(left)      -> '.' n=name '(' as=args ')' e={ Send(left,n,as) }  sendToOrGetFrom^(e);
  args              -> e=exp es=(',' exp)* { e:es } | {[]};
  getFrom(left)     -> '.' n=name e=(setExp^(left,n) | { Get(left,n) }) sendToOrGetFrom^(e);
  setExp(o,n)       -> whitespace ':=' e=exp { Set(o,n,e) };
  apply             -> e=lookupExp optApply^(e);
  optApply(left)    -> '(' as=args ')' { Apply(left,as) } | { left };
  lookupExp         -> e=simpleExp optLookup^(e);
  optLookup(left)   -> whitespace '::' n=name e={Lookup(left,n)} optLookup^(e) | {left};
  simpleExp         -> var | intExp | strExp | boolExp | list | op | ifexp | letexp | caseexp | findexp | block;
  var               -> n=name { Var(n) };
  strExp            -> '\'' cs=(not(39) stringChar)* 39 {Str(cs)};
  string            -> '\'' cs=(not(39) stringChar)* 39 {'values.Str'(cs)};
  strings           -> s=string ss=(x=string ',' {x})* {s:ss};
  intExp            -> i=int { Int(i) };
  boolExp           -> 'true' { Bool(true) } | 'false' { Bool(false) };
  list              -> '[' as=args ']' { List(as) };
  for               -> whitespace 'for' lparen n=name colon t=type whitespace 'in' e=exp rparen b=command { For(n,t,e,b) };
  op                -> 'op' n=opName opNamed^(n);
  opNamed(n)        -> lparen fs=formals rparen whitespace ':' t=type opBody^(n,fs,t);
  opBody(n,fs,t)    -> eql e=exp { Op(n,fs,t,e) } | e=block { Op(n,fs,t,e) };
  opName            -> name | { 'anonynous' };
  headlessOp        -> n=name opNamed^(n);
  formals           -> f=formal fs=(',' formal)* { f:fs } | {[]};
  formal            -> n=name ':' t=type { Formal(n,t) };
  type              -> n=name { NamedType(n) } | lsquare t=type rsquare { ListType(t) };
  block             -> lcurl es=command* rcurl { Block(es) };
  command           -> block | for | ifcommand | letcommand | e=exp semi {e} | e=update semi {e};
  update            -> n=name whitespace ':=' e=exp { Update(n,e) };
  ifexp             -> whitespace 'if' test=exp whitespace 'then' conseq=exp whitespace 'else' alt=exp { If(test,conseq,alt) };
  ifcommand         -> whitespace 'if' test=exp whitespace 'then' conseq=command whitespace 'else' alt=command { If(test,conseq,alt) };
  letexp            -> whitespace 'let' bs=letEBindings^([]);
  letcommand        -> whitespace 'let' bs=letCBindings^([]);
  letEBindings(bs)  -> b=binding (semi letEBindings^(b:bs) | whitespace 'then' l=letEBindings^([]) { Let(b:bs,l) } | whitespace 'in' e=exp { Let(b:bs,e) });
  letCBindings(bs)  -> b=binding (semi letCBindings^(b:bs) | whitespace 'then' l=letCBindings^([]) { Let(b:bs,l) } | whitespace 'in' c=command { Let(b:bs,c) });
  binding           -> n=name colon t=type eql e=exp { Binding(n,t,e) };
  caseexp           -> whitespace 'case' e=exp lcurl as=arms whitespace 'else' d=exp rcurl { Case(e,as,d) };
  arms              -> a=arm as=(semi arm)* { a:as } | {[]};
  arm               -> p=pattern arrow e=exp { Arm(p,e) };
  pattern           -> pConst | pVar;
  pConst            -> pStr | pInt;
  pStr              -> '\'' cs=(not(39) stringChar)* 39 { PStr(cs) };
  pInt              -> i=int { PInt(i) };
  pVar              -> n=name { PVar(n) };
  findexp           -> whitespace 'find' n=name colon t=type whitespace 'in' e=exp whitespace 'when' p=exp lcurl b=exp rcurl whitespace 'else' d=exp { Find(n,t,e,p,b,d) };
  
  whitespace  -> (32 | 10 | 13 | 9 | comment)* !;
  comment     -> '//' ([32,126] !)* !;
  lcurl       -> whitespace '{';
  rcurl       -> whitespace '}';
  lsquare     -> whitespace '[';
  rsquare     -> whitespace ']';
  lparen      -> whitespace '(';
  rparen      -> whitespace ')';
  colon       -> whitespace ':';
  semi        -> whitespace ';';
  eql         -> whitespace '=';
  comma       -> whitespace ',';
  arrow       -> whitespace '->';
  keyWord     -> key not([97,122] | [65,90]);
  key         -> 'EOF' | 'class' | 'case' | 'true' | 'false' | 'op' | 'quit' | 'import' | 'in' | 'for' | 'if' | 'then' | 'else' | 'extends' | 'find' | 'let';
  name        -> whitespace not(keyWord) c=alphaChar chars=(alphaChar | numChar)*  whitespace ! {'values.Str'(c:chars)};
  int         -> whitespace i=[48,57]+ {'values.Int'(i)};
  alphaChar   -> [65,90] | [97,122];
  stringChar  -> 92 c=. ! {c} | .;
  numChar     -> [48,57]
  
}

test() = 
  let c = jmf.parse('class C {}',[]).eval()
  in print(send(c,'new',[]))