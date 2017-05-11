package dk.dtu.compute.mbse.yawl.constraints;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.pnml.tools.epnk.helpers.FlatAccess;
import org.pnml.tools.epnk.helpers.NetFunctions;
import org.pnml.tools.epnk.pnmlcoremodel.Node;

import dk.dtu.compute.mbse.yawl.AType;
import dk.dtu.compute.mbse.yawl.Arc;
import dk.dtu.compute.mbse.yawl.functions.YAWLFunctions;

/**
 * This is a constraint saying that a YAWL net should have exactly one start
 * place and one end place.
 *
 * @author gruppe20
 * @generated NOT
 */
public class NoDuplicateArcs extends AbstractModelConstraint {
	public IStatus validate(IValidationContext ctx) {
		EObject object = ctx.getTarget();

		if (object instanceof Arc) {
			Arc arc = (Arc) object;
			AType arcType = YAWLFunctions.getType(arc);
			Node source = NetFunctions.resolve(arc.getSource());
			Node target = NetFunctions.resolve(arc.getTarget());
			FlatAccess flatAccess = FlatAccess.getFlatAccess(NetFunctions.getPetriNet(arc));

			if ((arcType == AType.NORMAL || arcType == AType.RESET) && source != null && flatAccess != null) {
				for (org.pnml.tools.epnk.pnmlcoremodel.Arc other : flatAccess.getOut(source)) {
					if (other != arc) {
						if (other instanceof Arc) {
							Arc arc2 = (Arc) other;
							Node target2 = NetFunctions.resolve(arc2.getTarget());
							if (target == target2) {
								if (YAWLFunctions.getType(arc) == YAWLFunctions.getType(arc2)) {
									return ctx.createFailureStatus(new Object[] { arc });
								}
							}
						}
					}
				}
			}
		}
		return ctx.createSuccessStatus();
	}
}