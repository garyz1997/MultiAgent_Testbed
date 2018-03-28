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
    C2_afterM : BOOL := false;
    Step1 : BOOL := false;
    Step2 : BOOL := false;
    Step3 : BOOL := false;
    Step4 : BOOL := false;
    Pause1 : BOOL := false;
    Pause2 : BOOL := false;
  END_VAR
  VAR
    jadeProg1 AT %IX0.7 : BOOL := true;
    jadeProg2 AT %IX1.1 : BOOL := true;
    jadeProg3 AT %IX1.2 : BOOL := true;
    jadeProg4 AT %IX1.4 : BOOL := true;
  END_VAR
  VAR
    Timer_conveyor : TON;
    TON0 : TON;
    Timer_machining : TON;
    TON2 : TON;
    TON4 : TON;
  END_VAR

  IF jadeProg1 THEN
    Step1 := TRUE; (*set*)
  END_IF;
  IF jadeProg2 THEN
    Step2 := TRUE; (*set*)
  END_IF;
  IF jadeProg3 THEN
    Step3 := TRUE; (*set*)
  END_IF;
  IF jadeProg4 THEN
    Step4 := TRUE; (*set*)
  END_IF;
  IF NOT(C1_IR) THEN
    C2_working := TRUE; (*set*)
  END_IF;
  C2_motor := NOT((C2_afterM OR C2_working) AND NOT(Main));
  IF NOT(M1_IR) AND C2_working THEN
    C2_working := FALSE; (*reset*)
  END_IF;
  M1_motor := NOT((Step1 OR Step2 OR Step3 OR Step4) AND NOT(Main));
  TON0(IN := Step1, PT := T#1000ms);
  IF TON0.Q THEN
    Pause1 := TRUE; (*set*)
  END_IF;
  IF TON0.Q THEN
    Step1 := FALSE; (*reset*)
  END_IF;
  TON2(IN := Step2, PT := T#5000ms);
  IF TON2.Q THEN
    Step2 := FALSE; (*reset*)
  END_IF;
  Timer_machining(IN := Step3, PT := T#10000ms);
  IF Timer_machining.Q THEN
    Step3 := FALSE; (*reset*)
  END_IF;
  IF Step4 THEN
    C2_afterM := TRUE; (*set*)
  END_IF;
  IF Step4 THEN
    Step4 := FALSE; (*reset*)
  END_IF;
  Timer_conveyor(IN := C2_working, PT := T#6000ms);
  IF Timer_conveyor.Q THEN
    C2_working := FALSE; (*reset*)
  END_IF;
  TON4(IN := C2_afterM, PT := T#2000ms);
  IF TON4.Q THEN
    C2_afterM := FALSE; (*reset*)
  END_IF;
END_PROGRAM


CONFIGURATION Config0

  RESOURCE Res0 ON PLC
    TASK TaskMain(INTERVAL := T#50ms,PRIORITY := 0);
    PROGRAM Inst0 WITH TaskMain : Cell2;
  END_RESOURCE
END_CONFIGURATION
