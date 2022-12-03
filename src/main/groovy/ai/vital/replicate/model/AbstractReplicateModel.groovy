package ai.vital.replicate.model

abstract class AbstractReplicateModel {
	
	abstract String getModelName()
	
	abstract String getModelVersion()
	
	abstract Map<String,Object> getParameterMap()
		
	
	
}
