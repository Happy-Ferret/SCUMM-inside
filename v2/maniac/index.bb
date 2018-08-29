AppTitle "MM v2 Index"
win = CreateWindow("MM v2 Index", 200, 100, 230, 360, 0, 1) 
area = CreateTextArea(10, 10, 200, 310, win)
Local file[178]

index = ReadFile("00.LFL")
	If Not index Then RuntimeError "Datei nicht gefunden!"

	ReadShort(index) ;C64 Daten
	keep = (ReadShort(index) Xor $FFFF) - 1: AddTextAreaText area, "Anzahl der Objekte ["+(keep+1)+"]" +Chr$(10)
	For i = 0 To keep
		ownerState = ReadByte(index) Xor $FF
		AddTextAreaText area, "Obj["+i+"]  owner["+(ownerState And $0F)+"]  state["+(ownerState Shr 4)+"]" +Chr$(10)
		;                                  owner=Nibble 2                   state=Nibble 1
	Next

	keep = (ReadByte(index) Xor $FF) - 1
	AddTextAreaText area, Chr$(10)+ "Anzahl der Räume ["+(keep+1)+"]" +Chr$(10)
	For i = 0 To keep
		file[i] = ReadByte(index): If file[i] Then file[i] = (file[i] Xor $FF) And $0F ;ASCII
		AddTextAreaText area, "Raum["+i+"]  Disk["+file[i]+"]  Off[0]" +Chr$(10)
	Next
	SeekFile(index, FilePos(index)+keep*2+2) ;C64 Daten (Offset immer 0)

	keep = (ReadByte(index) Xor $FF) - 1
	AddTextAreaText area, Chr$(10)+ "Anzahl der Kostüme ["+(keep+1)+"]" +Chr$(10)
	For i = 0 To keep
		file[i] = ReadByte(index): If file[i] Then file[i] = file[i] Xor $FF
	Next
	For i = 0 To keep
		off = ReadShort(index): If off Then off = off Xor $FFFF
		AddTextAreaText area, "Kostüm["+i+"]  Raum["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next
	
	keep = (ReadByte(index) Xor $FF) - 1
	AddTextAreaText area, Chr$(10)+ "Anzahl der Skripte ["+(keep+1)+"]" +Chr$(10)
	For i = 0 To keep
		file[i] = ReadByte(index): If file[i] Then file[i] = file[i] Xor $FF
	Next
	For i = 0 To keep
		off = ReadShort(index): If off Then off = off Xor $FFFF
		AddTextAreaText area, "Skript["+i+"]  Raum["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next

	keep = (ReadByte(index) Xor $FF) - 1
	AddTextAreaText area, Chr$(10)+ "Anzahl der Sounds ["+(keep+1)+"]" +Chr$(10)
	For i = 0 To keep
		file[i] = ReadByte(index): If file[i] Then file[i] = file[i] Xor $FF
	Next
	For i = 0 To keep
		off = ReadShort(index): If off Then off = off Xor $FFFF
		AddTextAreaText area, "Sound["+i+"]  Raum["+file[i]+"]  Off["+off+"]" +Chr$(10)
	Next
CloseFile index

Repeat Until WaitEvent() = $803