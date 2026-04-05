package desperados.scb;

import java.util.List;

public class ScbQuadService {

	public static ScbQuad parseQuad(ScbFunction scbFunction, byte[] data) {
		
		ScbQuad quad = new ScbQuad(scbFunction);
		String statement;
		int code = data[0] & 0xFF;
		
		switch (code) {
		case 0:	// unused
			statement = "Flush";
			break;
		case 1:
			statement = "NOP";
			break;
		case 2:
			statement = "PARAM %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			break;
		case 3:
			statement = "InitFunction numVolVars: %s, numTempVars: %s";
			quad.addOperand(new ScbOperand(data[1], data[2])); // SIZE OF VOLATILE VARIABLE
			quad.addOperand(new ScbOperand(data[3], data[4])); // SIZE OF TEMPORARY VARIABLE
			break;
		case 4:
			statement = "EndFunction";
			break;
		case 5:
			statement = "CALL %s";
			quad.addOperand(new ScbOperand(data[1], data[2], data[3], data[4]));
			break;
		case 6:
			statement = "RETURN";
			break;
		case 7:
			statement = "RETURN %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			break;
		case 8:
			statement = "%s := GETPARAM %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[5], data[6])); //, data[7], data[8], false, true));
			break;
		case 9:	// only used 2 times
			statement = "SETPARAM %s := %s";
			quad.addOperand(new ScbOperand(data[5], data[6])); //, data[7], data[8], false, true));
			quad.addOperand(new ScbOperand(data[1], data[2]));
			break;
		case 0x0A:
			statement = "%s := GETRETURN";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			break;
		case 0x0B:
			statement = "NATIVEPARAM %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			break;
		case 0x0C:
			statement = "NATIVECALL %s";
			quad.addOperand(new ScbOperand(data[1], data[2], data[3], data[4]));
			break;
		case 0x0D:
			statement = "%s := NATIVEGETRETURN";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			break;
		case 0x0E:
			statement = "GOTO %s";
			ScbOperand gotoOperand = new ScbOperand(data[1], data[2], data[3], data[4]);
			quad.setLabel(gotoOperand.getValue());
			quad.addOperand(gotoOperand);
			break;
		case 0x0F:
			statement = "IF ( %s != 0 ) THEN GOTO %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			gotoOperand = new ScbOperand(data[5], data[6], data[7], data[8]);
			quad.setLabel(gotoOperand.getValue());
			quad.addOperand(gotoOperand);
			break;
		case 0x10:	// unused
			statement = "IF ( %s == 0 ) THEN GOTO %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			gotoOperand = new ScbOperand(data[5], data[6], data[7], data[8]);
			quad.setLabel(gotoOperand.getValue());
			quad.addOperand(gotoOperand);
			break;
		case 0x11:
			statement = "%s := %s";	// assign value of variable to another variable
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			break;
		case 0x12:	// unused
			statement = "%s :=F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			break;
		case 0x13:
			statement = "%s :=I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[5], data[6], data[7], data[8]));
			break;
		case 0x14:
			statement = "%s :=F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[5], data[6], data[7], data[8], true, false));
			break;
		case 0x15:
			statement = "%s :=I - %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			break;
		case 0x16:	// unused
			statement = "%s :=F - %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			break;
		case 0x17:	// unused
			statement = "%s := (INT) %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			break;
		case 0x18:	// unused
			statement = "%s := (FLOAT) %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			break;
		case 0x19:
			statement = "%s := %s +I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x1A:
			statement = "%s := %s -I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x1B:	// only used 3 times
			statement = "%s := %s *I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x1C:	// only used 8 times
			statement = "%s := %s /I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x1D:	// unused
			statement = "%s := %s +F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x1E:	// unused
			statement = "%s := %s -F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x1F:	// unused
			statement = "%s := %s *F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x20:	// unused
			statement = "%s := %s /F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x21:
			statement = "%s := %s <=I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x22:
			statement = "%s := %s <I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x23:	// only used 7 times
			statement = "%s := %s >=I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x24:
			statement = "%s := %s >I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x25:
			statement = "%s := %s !=I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x26:
			statement = "%s := %s ==I %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x27:	// unused
			statement = "%s := %s >F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x28:	// unused
			statement = "%s := %s >=F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x29:	// unused
			statement = "%s := %s <F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x2A:	// unused
			statement = "%s := %s <=F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x2B:	// unused
			statement = "%s := %s ==F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		case 0x2C:	// unused
			statement = "%s := %s !=F %s";
			quad.addOperand(new ScbOperand(data[1], data[2]));
			quad.addOperand(new ScbOperand(data[3], data[4]));
			quad.addOperand(new ScbOperand(data[5], data[6]));
			break;
		default:
			statement = "Unknown SCQuad"; // error
			break;
		}
		
		quad.setStatement(statement);
		
		return quad;
	}

	public static String getString(ScbQuad quad) {
		String statement = quad.getStatement();
		String label = quad.getLabel();
		List<ScbOperand> operands = quad.getOperands();
		
		if (statement.startsWith("NATIVECALL")) {
			return "NATIVECALL " + ScbExternalFunction.functions[operands.get(0).getValue()];
		}
		
		if (statement.startsWith("CALL")) {
			return "CALL " + quad.getFunction().getScbClass().getFunctionName(operands.get(0).getValue());
		}
		
		if (label != null) {
			if (operands.size() == 1) {
				return String.format(statement, label);
			}
			return String.format(statement, operands.get(0), label);
		}
		
		switch(operands.size()) {
		case 1:
			return String.format(statement, operands.get(0));
		case 2:
			return String.format(statement, operands.get(0), operands.get(1));
		case 3:
			return String.format(statement, operands.get(0), operands.get(1), operands.get(2));
		default:
			return statement;
		}
	}
}
