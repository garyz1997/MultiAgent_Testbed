PROGRAM Cell3
  VAR
    C2_IR AT %IX0.3 : BOOL := true;
    M2_IR AT %IX0.4 : BOOL := true;
    Main AT %IX0.6 : BOOL := true;
    M2_motor AT %QX0.2 : BOOL := true;
    C3_motor AT %QX0.4 : BOOL := true;
  END_VAR
  VAR
    C3_working : BOOL := false;
  END_VAR
  VAR
    C3_working_int AT %MD1 : DINT := 0;
  END_VAR
  VAR
    C3_afterM : BOOL := false;
  END_VAR
  VAR
    C3_afterM_int AT %MD2 : DINT := 0;
  END_VAR
  VAR
    Step1 : BOOL := false;
  END_VAR
  VAR
    Step1_int AT %MD3 : DINT := 0;
  END_VAR
  VAR
    Step2 : BOOL := false;
  END_VAR
  VAR
    Step2_int AT %MD4 : DINT := 0;
  END_VAR
  VAR
    Pause : BOOL := false;
  END_VAR
  VAR
    Pause_int AT %MD5 : DINT := 0;
  END_VAR
  VAR
    Timer_conveyor : TON;
    Timer_machining : TON;
    TON1 : TON;
    TON2 : TON;
    TON3 : TON;
  END_VAR
  VAR
    JadeProg1 AT %IX0.7 : BOOL := true;
  END_VAR
  VAR
    TON4 : TON;
    BOOL_TO_DINT70_ENO : BOOL;
    BOOL_TO_DINT70_OUT : DINT;
    BOOL_TO_DINT41_ENO : BOOL;
    BOOL_TO_DINT41_OUT : DINT;
    BOOL_TO_DINT51_ENO : BOOL;
    BOOL_TO_DINT51_OUT : DINT;
    BOOL_TO_DINT75_ENO : BOOL;
    BOOL_TO_DINT75_OUT : DINT;
    BOOL_TO_DINT80_ENO : BOOL;
    BOOL_TO_DINT80_OUT : DINT;
  END_VAR

  TON4(IN := JadeProg1, PT := T#5000ms);
  IF TON4.Q THEN
    C3_working := TRUE; (*set*)
  END_IF;
  C3_motor := NOT((C3_afterM OR C3_working) AND NOT(Main));
  IF NOT(M2_IR) AND C3_working THEN
    Step1 := TRUE; (*set*)
  END_IF;
  IF NOT(M2_IR) AND C3_working THEN
    C3_working := FALSE; (*reset*)
  END_IF;
  M2_motor := NOT((Step1 OR Step2) AND NOT(Main));
  Timer_machining(IN := Step1, PT := T#2000ms);
  IF Timer_machining.Q THEN
    Pause := TRUE; (*set*)
  END_IF;
  IF Timer_machining.Q THEN
    Step1 := FALSE; (*reset*)
  END_IF;
  TON2(IN := Pause, PT := T#1000ms);
  IF TON2.Q THEN
    Step2 := TRUE; (*set*)
  END_IF;
  IF TON2.Q THEN
    Pause := FALSE; (*reset*)
  END_IF;
  TON1(IN := Step2, PT := T#2000ms);
  IF TON1.Q THEN
    C3_afterM := TRUE; (*set*)
  END_IF;
  IF TON1.Q THEN
    Step2 := FALSE; (*reset*)
  END_IF;
  Timer_conveyor(IN := C3_working, PT := T#6000ms);
  IF Timer_conveyor.Q THEN
    C3_working := FALSE; (*reset*)
  END_IF;
  TON3(IN := C3_afterM, PT := T#3000ms);
  IF TON3.Q THEN
    C3_afterM := FALSE; (*reset*)
  END_IF;
  BOOL_TO_DINT70_OUT := BOOL_TO_DINT(EN := TRUE, IN := C3_working, ENO => BOOL_TO_DINT70_ENO);
  C3_working_int := BOOL_TO_DINT70_OUT;
  BOOL_TO_DINT41_OUT := BOOL_TO_DINT(EN := TRUE, IN := C3_afterM, ENO => BOOL_TO_DINT41_ENO);
  C3_afterM_int := BOOL_TO_DINT41_OUT;
  BOOL_TO_DINT51_OUT := BOOL_TO_DINT(EN := TRUE, IN := Step1, ENO => BOOL_TO_DINT51_ENO);
  Step1_int := BOOL_TO_DINT51_OUT;
  BOOL_TO_DINT75_OUT := BOOL_TO_DINT(EN := TRUE, IN := Step2, ENO => BOOL_TO_DINT75_ENO);
  Step2_int := BOOL_TO_DINT75_OUT;
  BOOL_TO_DINT80_OUT := BOOL_TO_DINT(EN := TRUE, IN := Pause, ENO => BOOL_TO_DINT80_ENO);
  Pause_int := BOOL_TO_DINT80_OUT;
END_PROGRAM


CONFIGURATION Config0

  RESOURCE Res0 ON PLC
    TASK TaskMain(INTERVAL := T#50ms,PRIORITY := 0);
    PROGRAM Inst0 WITH TaskMain : Cell3;
  END_RESOURCE
END_CONFIGURATION
