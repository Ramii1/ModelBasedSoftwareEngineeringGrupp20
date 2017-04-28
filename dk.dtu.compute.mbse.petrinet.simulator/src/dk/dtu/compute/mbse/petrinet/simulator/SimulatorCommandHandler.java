package dk.dtu.compute.mbse.petrinet.simulator;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.View;

import dk.dtu.compute.mbse.petrinet.Arc;
import dk.dtu.compute.mbse.petrinet.Node;
import dk.dtu.compute.mbse.petrinet.PetrinetFactory;
import dk.dtu.compute.mbse.petrinet.Place;
import dk.dtu.compute.mbse.petrinet.Token;
import dk.dtu.compute.mbse.petrinet.Transition;
import dk.dtu.compute.mbse.petrinet.commands.FireTransitionCommand;

public class SimulatorCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Transition transition = getTransition(event.getApplicationContext());
		if (isEnabled(transition)) {
			EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(transition);
			if (domain != null) {
				domain.getCommandStack().execute(new FireTransitionCommand(domain, transition));
			}
			fire(transition);
		}
		return null;
	}

	@Override
	public void setEnabled(Object context) {
		Transition transition = getTransition(context);
		setBaseEnabled(isEnabled(transition));
	}
		
	static private boolean isEnabled(Transition transition) {
		if (transition != null) {
			for(Arc arc  : transition.getIn()){
				Node node = arc.getSource();
				if(node instanceof Place){
					Place place = (Place) node;
					if(place.getTokens().isEmpty()){
						return false;
					}
				}
			}
		}
		return true;
	}

	static private void fire(Transition transition) {
		if (transition != null && isEnabled(transition)) {
			for(Arc arc  : transition.getIn()){
				Node node = arc.getSource();
				if(node instanceof Place){
					Place place = (Place) node;
					if(!place.getTokens().isEmpty()){
						place.getTokens().remove(0);

					}
				}
			}

			for(Arc arc  : transition.getOut()){
				Node node = arc.getTarget();
				if(node instanceof Place){
					Place place = (Place) node;
					Token token = PetrinetFactory.eINSTANCE.createToken();
					place.getTokens().add(token);

				}
			}
		}
	}

	static private Transition getTransition(Object context) {
		if (context instanceof IEvaluationContext) {
			IEvaluationContext evaluationContext = (IEvaluationContext) context;
			Object object = evaluationContext.getDefaultVariable();
			if (object instanceof List) {
				@SuppressWarnings("rawtypes")
				List list = (List) object;
				if (list.size() == 1) {
					object = list.get(0);
					if (object instanceof Transition) {
						return (Transition) object;
					} else if (object instanceof IGraphicalEditPart) {
						IGraphicalEditPart editPart = (IGraphicalEditPart) object;
						Object model = editPart.getModel();
						if (model instanceof View) {
							Object element = ((View) model).getElement();
							if (element instanceof Transition) {
								return (Transition) element;
							}
						}
					}
				}
			}
		}
		return null;
	}

}
