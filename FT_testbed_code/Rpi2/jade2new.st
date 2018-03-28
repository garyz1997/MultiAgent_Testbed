PROGRAM Cell2
  VAR
    C1_IR AT %IX0.3 : BOOL := true;
    M1_IR AT %IX0.4 : BOOL := true;
    Main AT %IX0.6 : BOOL := true;
    M1_motor AT %QX0.2 : BOOL := true;
    C2_motor AT %QX0.4 : BOOL := true;
  END_VAR
  VAR
    C2_working : BOOL := false;
  END_VAR
  VAR
    C2_working_int AT %MD1 : DINT := 0;
  END_VAR
  VAR
    C2_afterM : BOOL := false;
  END_VAR
  VAR
    C2_afterM_int AT %MD2 : DINT := 0;
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
    Step3 : BOOL := false;
  END_VAR
  VAR
    Step3_int AT %MD5 : DINT := 0;
  END_VAR
  VAR
    Pause1 : BOOL := false;
  END_VAR
  VAR
    Pause1_int AT %MD6 : DINT := 0;
  END_VAR
  VAR
    Pause2 : BOOL := false;
  END_VAR
  VAR
    Pause2_int AT %MD7 : DINT := 0;
  END_VAR
  VAR
    Timer_conveyor : TON;
    Timer_machining : TON;
    TON0 : TON;
    TON1 : TON;
    TON2 : TON;
    TON3 : TON;
    TON4 : TON;
  END_VAR
  VAR
    jadeProg1 AT %IX0.7 : BOOL := true;
  END_VAR
  VAR
    BOOL_TO_DINT41_ENO : BOOL;
    BOOL_TO_DINT41_OUT : DINT;
    BOOL_TO_DINT105_ENO : BOOL;
    BOOL_TO_DINT105_OUT : DINT;
    BOOL_TO_DINT51_ENO : BOOL;
    BOOL_TO_DINT51_OUT : DINT;
    BOOL_TO_DINT85_ENO : BOOL;
    BOOL_TO_DINT85_OUT : DINT;
    BOOL_TO_DINT90_ENO : BOOL;
    BOOL_TO_DINT90_OUT : DINT;
    BOOL_TO_DINT95_ENO : BOOL;
    BOOL_TO_DINT95_OUT : DINT;
    BOOL_TO_DINT100_ENO : BOOL;
    BOOL_TO_DINT100_OUT : DINT;
  END_VAR

  IF jadeProg1 THEN
    C2_working := TRUE; (*set*)
  END_IF;
  C2_motor := NOT((C2_afterM OR C2_working) AND NOT(Main));
  IF NOT(M1_IR) AND C2_working THEN
    Step1 := TRUE; (*set*)
  END_IF;
  IF NOT(M1_IR) AND C2_working THEN
    C2_working := FALSE; (*reset*)
  END_IF;
  M1_motor := NOT((Step1 OR Step2 OR Step3) AND NOT(Main));
  TON0(IN := Step1, PT := T#1000ms);
  IF TON0.Q THEN
    Pause1 := TRUE; (*set*)
  END_IF;
  IF TON0.Q THEN
    Step1 := FALSE; (*reset*)
  END_IF;
  TON1(IN := Pause1, PT := T#1000ms);
  IF TON1.Q THEN
    Pause1 := FALSE; (*reset*)
  END_IF;
  IF TON1.Q THEN
    Step2 := TRUE; (*set*)
  END_IF;
  TON2(IN := Step2, PT := T#1000ms);
  IF TON2.Q THEN
    Pause2 := TRUE; (*set*)
  END_IF;
  IF TON2.Q THEN
    Step2 := FALSE; (*reset*)
  END_IF;
  TON3(IN := Pause2, PT := T#1000ms);
  IF TON3.Q THEN
    Pause2 := FALSE; (*reset*)
  END_IF;
  IF TON3.Q THEN
    Step3 := TRUE; (*set*)
  END_IF;
  Timer_machining(IN := Step3, PT := T#1000ms);
  IF Timer_machining.Q THEN
    C2_afterM := TRUE; (*set*)
  END_IF;
  IF Timer_machining.Q THEN
    Step3 := FALSE; (*reset*)
  END_IF;
  Timer_conveyor(IN := C2_working, PT := T#6000ms);
  IF Timer_conveyor.Q THEN
    C2_working := FALSE; (*reset*)
  END_IF;
  TON4(IN := C2_afterM, PT := T#2000ms);
  IF TON4.Q THEN
    C2_afterM := FALSE; (*reset*)
  END_IF;
  BOOL_TO_DINT41_OUT := BOOL_TO_DINT(EN := TRUE, IN := C2_working, ENO => BOOL_TO_DINT41_ENO);
  C2_working_int := BOOL_TO_DINT41_OUT;
  BOOL_TO_DINT105_OUT := BOOL_TO_DINT(EN := TRUE, IN := C2_afterM, ENO => BOOL_TO_DINT105_ENO);
  C2_afterM_int := BOOL_TO_DINT105_OUT;
  BOOL_TO_DINT51_OUT := BOOL_TO_DINT(EN := TRUE, IN := Step1, ENO => BOOL_TO_DINT51_ENO);
  Step1_int := BOOL_TO_DINT51_OUT;
  BOOL_TO_DINT85_OUT := BOOL_TO_DINT(EN := TRUE, IN := Step2, ENO => BOOL_TO_DINT85_ENO);
  Step2_int := BOOL_TO_DINT85_OUT;
  BOOL_TO_DINT90_OUT := BOOL_TO_DINT(EN := TRUE, IN := Step3, ENO => BOOL_TO_DINT90_ENO);
  Step3_int := BOOL_TO_DINT90_OUT;
  BOOL_TO_DINT95_OUT := BOOL_TO_DINT(EN := TRUE, IN := Pause1, ENO => BOOL_TO_DINT95_ENO);
  Pause1_int := BOOL_TO_DINT95_OUT;
  BOOL_TO_DINT100_OUT := BOOL_TO_DINT(EN := TRUE, IN := Pause2, ENO => BOOL_TO_DINT100_ENO);
  Pause2_int := BOOL_TO_DINT100_OUT;
END_PROGRAM


CONFIGURATION Config0

  RESOURCE Res0 ON PLC
    TASK TaskMain(INTERVAL := T#50ms,PRIORITY := 0);
    PROGRAM Inst0 WITH TaskMain : Cell2;
  END_RESOURCE
END_CONFIGURATION
