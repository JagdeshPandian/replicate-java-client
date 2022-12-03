package ai.vital.replicate.api

class ReplicateRequest {
	
	String modelName
	
	String modelVersion
	
	String requestJSON
	
	public ReplicateRequest(String modelName, String modelVersion, String requestJSON) {
		
		this.modelName = modelName
		
		this.modelVersion = modelVersion
		
		this.requestJSON = requestJSON
	
	}
		
}
