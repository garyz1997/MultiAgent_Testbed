PROGRAM Cell4
  VAR
    R2_front AT %IX0.2 : BOOL := true;
    R2_rear AT %IX0.3 : BOOL := true;
    C4_endIR AT %IX0.4 : BOOL := true;
    C3_IR AT %IX0.5 : BOOL := true;
    Main AT %IX0.6 : BOOL := false;
    R2_fwd_motor AT %QX0.2 : BOOL := true;
    R2_bwd_motor AT %QX0.3 : BOOL := true;
    C4_motor AT %QX0.4 : BOOL := true;
  END_VAR
  VAR
    C4_working : BOOL := false;
  END_VAR
  VAR
    C4_working_int AT %MD1 : DINT := 0;
  END_VAR
  VAR
    R2_in_transit : BOOL := false;
  END_VAR
  VAR
    R2_in_transit_int AT %MD3 : DINT := 0;
  END_VAR
  VAR
    R2_fwd : BOOL := false;
  END_VAR
  VAR
    R2_fwd_int AT %MD4 : DINT := 0;
  END_VAR
  VAR
    R2_bwd : BOOL := false;
  END_VAR
  VAR
    R2_bwd_int AT %MD5 : DINT := 0;
  END_VAR
  VAR
    Timer_in_transit : TON;
    BOOL_TO_DINT49_ENO : BOOL;
    BOOL_TO_DINT49_OUT : DINT;
    BOOL_TO_DINT64_ENO : BOOL;
    BOOL_TO_DINT64_OUT : DINT;
    BOOL_TO_DINT61_ENO : BOOL;
    BOOL_TO_DINT61_OUT : DINT;
    BOOL_TO_DINT67_ENO : BOOL;
    BOOL_TO_DINT67_OUT : DINT;
  END_VAR

  IF NOT(C3_IR) THEN
    R2_in_transit := TRUE; (*set*)
  END_IF;
  Timer_in_transit(IN := R2_in_transit, PT := T#6000ms);
  IF Timer_in_transit.Q THEN
    R2_fwd := TRUE; (*set*)
  END_IF;
  IF Timer_in_transit.Q THEN
    C4_working := TRUE; (*set*)
  END_IF;
  IF Timer_in_transit.Q THEN
    R2_in_transit := FALSE; (*reset*)
  END_IF;
  R2_fwd_motor := NOT(R2_fwd AND NOT(Main));
  IF NOT(R2_front) AND R2_fwd THEN
    R2_bwd := TRUE; (*set*)
  END_IF;
  IF NOT(R2_front) AND R2_fwd THEN
    R2_fwd := FALSE; (*reset*)
  END_IF;
  R2_bwd_motor := NOT(R2_bwd AND NOT(Main) AND TRUE);
  IF NOT(R2_rear) AND R2_bwd THEN
    R2_bwd := FALSE; (*reset*)
  END_IF;
  C4_motor := NOT(C4_working AND NOT(Main));
  IF NOT(C4_endIR) AND C4_working THEN
    C4_working := FALSE; (*reset*)
  END_IF;
  BOOL_TO_DINT49_OUT := BOOL_TO_DINT(EN := TRUE, IN := C4_working, ENO => BOOL_TO_DINT49_ENO);
  C4_working_int := BOOL_TO_DINT49_OUT;
  BOOL_TO_DINT64_OUT := BOOL_TO_DINT(EN := TRUE, IN := R2_in_transit, ENO => BOOL_TO_DINT64_ENO);
  R2_in_transit_int := BOOL_TO_DINT64_OUT;
  BOOL_TO_DINT61_OUT := BOOL_TO_DINT(EN := TRUE, IN := R2_fwd, ENO => BOOL_TO_DINT61_ENO);
  R2_fwd_int := BOOL_TO_DINT61_OUT;
  BOOL_TO_DINT67_OUT := BOOL_TO_DINT(EN := TRUE, IN := R2_bwd, ENO => BOOL_TO_DINT67_ENO);
  R2_bwd_int := BOOL_TO_DINT67_OUT;
END_PROGRAM


CONFIGURATION Config0

  RESOURCE Res0 ON PLC
    TASK TaskMain(INTERVAL := T#50ms,PRIORITY := 0);
    PROGRAM Inst0 WITH TaskMain : Cell4;
  END_RESOURCE
END_CONFIGURATION
