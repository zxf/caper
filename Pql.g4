grammar Pql;
command
    :   COMMAND expression;
expression
    :   or +;
or  :   and ( OR and )*;
and :   atom ( AND atom)*;
atom    :   condition | PBR expression NBR;
condition
    :   (VAR|pld) COMP (STRING|INT|list);
list    :   '[' (STRING|INT)* ']';
pld :   VAR '[' INT ':' INT ']';
COMMAND : 'find';  //def
OR      :  'or' -> channel(HIDDEN);
AND     :  'and' -> channel(HIDDEN);
PBR     :  '(' -> channel(HIDDEN);
NBR     :  ')' -> channel(HIDDEN);
INT     :   ('0'..'9')+;
VAR :   ('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_')+;
COMP  :   '>'|'<'|'>='|'<='|'='|'!='|'has'|'in';  //def
STRING  :   '"' (('A'..'Z'|'a'..'z') +) '"';
WS  :   [ \n\r\t\,] -> channel(HIDDEN) ;
