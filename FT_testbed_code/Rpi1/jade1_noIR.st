PROGRAM Cell1
  VAR
    R1_front AT %IX0.2 : BOOL := true;
    R1_rear AT %IX0.3 : BOOL := true;
    C1_endIR AT %IX0.4 : BOOL := true;
    C1_startIR AT %IX0.5 : BOOL := true;
    Main AT %IX0.6 : BOOL := false;
    R1_fwd_motor AT %QX0.2 : BOOL := true;
    R1_bwd_motor AT %QX0.3 : BOOL := true;
    C1_motor AT %QX0.4 : BOOL := true;
  END_VAR
  VAR
    C1_working : BOOL := false;
    State1 : BOOL := false;
  END_VAR
  VAR
    State1_int AT %MD1 : DINT := 0;
  END_VAR
  VAR
    R1_in_transit : BOOL := false;
    State2 : BOOL := false;
  END_VAR
  VAR
    State2_int AT %MD3 : DINT := 0;
  END_VAR
  VAR
    R1_fwd : BOOL := false;
  END_VAR
  VAR
    R1_fwd_int AT %MD4 : DINT := 0;
  END_VAR
  VAR
    R1_bwd : BOOL := false;
  END_VAR
  VAR
    R1_bwd_int AT %MD5 : DINT := 0;
  END_VAR
  VAR
    Timer_in_transit : TON;
  END_VAR
  VAR
    jadeProg1 AT %IX0.7 : BOOL := true;
  END_VAR
  VAR
    BOOL_TO_DINT27_ENO : BOOL;
    BOOL_TO_DINT27_OUT : DINT;
    BOOL_TO_DINT37_ENO : BOOL;
    BOOL_TO_DINT37_OUT : DINT;
    BOOL_TO_DINT61_ENO : BOOL;
    BOOL_TO_DINT61_OUT : DINT;
    BOOL_TO_DINT67_ENO : BOOL;
    BOOL_TO_DINT67_OUT : DINT;
  END_VAR

  IF jadeProg1 THEN
    C1_working := TRUE; (*set*)
  END_IF;
  C1_motor := NOT(C1_working AND NOT(Main));
  IF C1_working AND NOT(C1_endIR) THEN
    R1_in_transit := TRUE; (*set*)
  END_IF;
  Timer_in_transit(IN := R1_in_transit AND C1_working, PT := T#1000ms);
  IF Timer_in_transit.Q THEN
    R1_fwd := TRUE; (*set*)
  END_IF;
  IF Timer_in_transit.Q THEN
    C1_working := FALSE; (*reset*)
  END_IF;
  IF Timer_in_transit.Q THEN
    R1_in_transit := FALSE; (*reset*)
  END_IF;
  R1_fwd_motor := NOT(R1_fwd AND NOT(Main));
  IF NOT(R1_front) AND R1_fwd THEN
    R1_bwd := TRUE; (*set*)
  END_IF;
  IF NOT(R1_front) AND R1_fwd THEN
    R1_fwd := FALSE; (*reset*)
  END_IF;
  R1_bwd_motor := NOT(R1_bwd AND NOT(Main));
  IF NOT(R1_rear) AND R1_bwd THEN
    R1_bwd := FALSE; (*reset*)
  END_IF;
  IF R1_in_transit AND C1_working THEN
    State1 := FALSE; (*reset*)
  END_IF;
  IF R1_in_transit AND C1_working THEN
    State2 := TRUE; (*set*)
  END_IF;
  IF NOT(R1_in_transit) AND C1_working THEN
    State1 := TRUE; (*set*)
  END_IF;
  IF NOT(R1_in_transit) AND NOT(C1_working) THEN
    State2 := FALSE; (*reset*)
  END_IF;
  BOOL_TO_DINT27_OUT := BOOL_TO_DINT(EN := TRUE, IN := State1, ENO => BOOL_TO_DINT27_ENO);
  State1_int := BOOL_TO_DINT27_OUT;
  BOOL_TO_DINT37_OUT := BOOL_TO_DINT(EN := TRUE, IN := State2, ENO => BOOL_TO_DINT37_ENO);
  State2_int := BOOL_TO_DINT37_OUT;
  BOOL_TO_DINT61_OUT := BOOL_TO_DINT(EN := TRUE, IN := R1_fwd, ENO => BOOL_TO_DINT61_ENO);
  R1_fwd_int := BOOL_TO_DINT61_OUT;
  BOOL_TO_DINT67_OUT := BOOL_TO_DINT(EN := TRUE, IN := R1_bwd, ENO => BOOL_TO_DINT67_ENO);
  R1_bwd_int := BOOL_TO_DINT67_OUT;
END_PROGRAM


CONFIGURATION Config0

  RESOURCE Res0 ON PLC
    TASK TaskMain(INTERVAL := T#50ms,PRIORITY := 0);
    PROGRAM Inst0 WITH TaskMain : Cell1;
  END_RESOURCE
END_CONFIGURATION
