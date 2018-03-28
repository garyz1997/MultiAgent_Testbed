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
    C3_afterM : BOOL := false;
    Step1 : BOOL := false;
    Step2 : BOOL := false;
    Step3 : BOOL := false;
    Pause : BOOL := false;
    Pause0 : BOOL := false;
    Timer_conveyor : TON;
    Timer_machining : TON;
    TON1 : TON;
    TON2 : TON;
    TON3 : TON;
  END_VAR
  VAR
    JadeProg1 AT %IX0.7 : BOOL := true;
    JadeProg2 AT %IX1.1 : BOOL := true;
    JadeProg3 AT %IX1.2 : BOOL := true;
  END_VAR
  VAR
    TON0 : TON;
    TON4 : TON;
  END_VAR

  TON0(IN := NOT(C2_IR), PT := T#6500ms);
  IF TON0.Q THEN
    C3_working := TRUE; (*set*)
  END_IF;
  C3_motor := NOT((C3_afterM OR C3_working) AND NOT(Main));
  IF NOT(M2_IR) AND C3_working THEN
    C3_working := FALSE; (*reset*)
  END_IF;
  M2_motor := NOT((Step1 OR Step2) AND NOT(Main));
  IF JadeProg1 THEN
    Step1 := TRUE; (*set*)
  END_IF;
  Timer_machining(IN := Step1, PT := T#2000ms);
  IF Timer_machining.Q THEN
    Pause := TRUE; (*set*)
  END_IF;
  IF Timer_machining.Q THEN
    Step1 := FALSE; (*reset*)
  END_IF;
  TON2(IN := Pause, PT := T#1000ms);
  IF TON2.Q THEN
    Pause := FALSE; (*reset*)
  END_IF;
  IF JadeProg2 THEN
    Step2 := TRUE; (*set*)
  END_IF;
  TON1(IN := Step2, PT := T#5000ms);
  IF TON1.Q THEN
    Pause0 := TRUE; (*set*)
  END_IF;
  IF TON1.Q THEN
    Step2 := FALSE; (*reset*)
  END_IF;
  TON4(IN := Pause0, PT := T#1000ms);
  IF TON4.Q THEN
    Pause0 := FALSE; (*reset*)
  END_IF;
  IF JadeProg3 THEN
    Step3 := TRUE; (*set*)
  END_IF;
  IF Step3 THEN
    C3_afterM := TRUE; (*set*)
  END_IF;
  IF Step3 THEN
    Step3 := FALSE; (*reset*)
  END_IF;
  Timer_conveyor(IN := C3_working, PT := T#6000ms);
  IF Timer_conveyor.Q THEN
    C3_working := FALSE; (*reset*)
  END_IF;
  TON3(IN := C3_afterM, PT := T#3000ms);
  IF TON3.Q THEN
    C3_afterM := FALSE; (*reset*)
  END_IF;
END_PROGRAM


CONFIGURATION Config0

  RESOURCE Res0 ON PLC
    TASK TaskMain(INTERVAL := T#50ms,PRIORITY := 0);
    PROGRAM Inst0 WITH TaskMain : Cell3;
  END_RESOURCE
END_CONFIGURATION
