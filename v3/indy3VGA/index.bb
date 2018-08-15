AppTitle "Indy3VGA Index"
win = CreateWindow("Indy3VGA Index", 150, 100, 280, 360, 0, 1) 
area = CreateTextArea(10, 10, 250, 310, win)

index = ReadFile("00.LFL")
	If Not index Then RuntimeError "Datei nicht gefunden!"

	ReadInt(index) ;Blockweite in Byte
	AddTextAreaText area, "Block ["+Chr$(ReadByte(index))+Chr$(ReadByte(index))+"]" +Chr$(10)
	keep = ReadShort(index) - 1: AddTextAreaText area, "Anzahl ["+(keep+1)+"]" +Chr$(10)
	For i = 0 To keep
		AddTextAreaText area, "Datei["+i+"]  Disk["+ReadByte(index)+"]  Off["+ReadInt(index)+"]" +Chr$(10)
	Next

	ReadInt(index)
	AddTextAreaText area, Chr$(10)+ "Block ["+Chr$(ReadByte(index))+Chr$(ReadByte(index))+"]" +Chr$(10)
	keep = ReadShort(index) - 1: AddTextAreaText area, "Anzahl ["+(keep+1)+"]" +Chr$(10)
	For i = 0 To keep
		AddTextAreaText area, "Skript["+i+"]  Datei["+ReadByte(index)+"]  Off["+ReadInt(index)+"]" +Chr$(10)
	Next

	ReadInt(index)
	AddTextAreaText area, Chr$(10)+ "Block ["+Chr$(ReadByte(index))+Chr$(ReadByte(index))+"]" +Chr$(10)
	keep = ReadShort(index) - 1: AddTextAreaText area, "Anzahl ["+(keep+1)+"]" +Chr$(10)
	For i = 0 To keep
		AddTextAreaText area, "Sound["+i+"]  Datei["+ReadByte(index)+"]  Off["+ReadInt(index)+"]" +Chr$(10)
	Next

	ReadInt(index)
	AddTextAreaText area, Chr$(10)+ "Block ["+Chr$(ReadByte(index))+Chr$(ReadByte(index))+"]" +Chr$(10)
	keep = ReadShort(index) - 1: AddTextAreaText area, "Anzahl ["+(keep+1)+"]" +Chr$(10)
	For i = 0 To keep
		AddTextAreaText area, "Ego["+i+"]  Datei["+ReadByte(index)+"]  Off["+ReadInt(index)+"]" +Chr$(10)
	Next

	ReadInt(index)
	AddTextAreaText area, Chr$(10)+ "Block ["+Chr$(ReadByte(index))+Chr$(ReadByte(index))+"]" +Chr$(10)
	keep = ReadShort(index) - 1: AddTextAreaText area, "Anzahl ["+(keep+1)+"]" +Chr$(10)
	For i = 0 To keep
		AddTextAreaText area, "Obj["+i+"]  class$["+RSet$(Hex$(ReadByte(index)+ReadByte(index) Shl 8+ReadByte(index) Shl 16), 6)
		;																			^Bytes zu LitteEndian konvertieren
		ownerState = ReadByte(index)
		AddTextAreaText area, "]  owner["+(ownerState And $0F)+"]  state["+(ownerState Shr 4)+"]" +Chr$(10)
		;                      owner=Nibble 2                   state=Nibble 1
	Next
CloseFile index

Repeat Until WaitEvent() = $803