package desperados.scb;

import java.util.ArrayList;
import java.util.List;

public class ScbQuad {

	private ScbFunction scbFunction;
	private String label;
	private String statement;
	private List<ScbOperand> operands;

	public ScbQuad(ScbFunction scbFunction) {
		this.scbFunction = scbFunction;
		operands = new ArrayList<ScbOperand>();
	}

	public void addOperand(ScbOperand operand) {
		operands.add(operand);
	}

	public List<ScbOperand> getOperands() {
		return operands;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public String getStatement() {
		return statement;
	}

	public void setLabel(int gotoAddress) {
		label = scbFunction.addLabel(gotoAddress);
	}

	public String getLabel() {
		return label;
	}

	public ScbFunction getFunction() {
		return scbFunction;
	}

	public String toString() {
		return ScbQuadService.getString(this);
	}
}
