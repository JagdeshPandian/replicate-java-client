package ai.vital.replicate.api

import groovy.json.JsonSlurper
import java.util.stream.Collectors
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.util.EntityUtils
import groovy.json.JsonOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ReplicateJavaClient {
	
	private final static Logger log = LoggerFactory.getLogger( ReplicateJavaClient.class)
	
	static JsonSlurper parser = new JsonSlurper()
	
	private String baseEndpoint = "https://api.replicate.com/v1/"

	boolean authStatus = false
		
	String apiKey = null
	
	String apiEndpoint = baseEndpoint
	
	String webhook = null
	
	CloseableHttpClient httpclient = null
	
	String modelName = null
	
	String modelVersion = null
	
	ReplicateJavaClient(String apiKey, String modelName, String modelVersion) {
		
		this.apiKey = apiKey
		
		this.modelName = modelName
		
		this.modelVersion = modelVersion
		
	}
		
	ReplicateJavaClient(String apiKey, String webhook, String modelName, String modelVersion) {
		
		this.apiKey = apiKey
		
		this.webhook = webhook
		
		this.modelName = modelName
		
		this.modelVersion = modelVersion
		
	}
	
	boolean authenticate() {
				
	}
	
	
	boolean isAuthenticated() {
	
		// test authentication
			
		return authStatus
		
	}
	

	String status() {
		
		
		
		
	}
	
	
	String postPrediction(String predictJSON) {
				
		try {
			
			httpclient = HttpClients.createDefault()
			
			HttpPost httppost = new HttpPost ( apiEndpoint + "predictions" )
			
			Map requestMap = [:]
			
			requestMap["version"] = modelVersion
			
			Map inputMap = parser.parseText(predictJSON)
			
			requestMap["input"] = inputMap
			
			String requestJSON = JsonOutput.toJson(requestMap)
			
			StringEntity entity = new StringEntity(requestJSON, "utf-8")
				
			httppost.setEntity(entity)
				
			httppost.setHeader("Authorization", "Token ${apiKey}")
			
			httppost.setHeader("Content-type", "application/json")
			
			CloseableHttpResponse response = httpclient.execute(httppost)
			
			String json_string = EntityUtils.toString( response.getEntity() )
			
			response.close()
						
			def prettyJSON = JsonOutput.prettyPrint(json_string)
			
			Map result = parser.parse(json_string.toCharArray())
			
			log.info( "Result:\n" + prettyJSON )
			
			return prettyJSON
			
		} catch(Exception ex) {
			
			log.error("Exception posting prediction: " + ex.localizedMessage )
			
			return null
		}
		
		return null
		
	}
	
	
	String getPrediction(String predictionIdentifier) {
		
		String getPredictionURL = "https://api.replicate.com/v1/predictions/${predictionIdentifier}"
		
		CloseableHttpClient httpclient = HttpClients.createDefault()
		
		HttpGet httpget = new HttpGet(getPredictionURL)
		
		httpget.setHeader("Authorization", "Token ${apiKey}")
		
		HttpResponse httpresponse = httpclient.execute(httpget);
		
		InputStream inputStream = httpresponse.getEntity().getContent()
		
		String result = new BufferedReader(new InputStreamReader(inputStream))
			.lines().collect(Collectors.joining("\n"));
	
		String prettyJSON = JsonOutput.prettyPrint(result)
		
		log.info( "JSON:\n" + prettyJSON )
		
		return prettyJSON
		
	}
	
	String generatePredictionPoll(String predictJSON, Integer timeout_ms) {
		
		String response = postPrediction(predictJSON) 

		Map responseMap = parser.parseText(response)		
		
		String predictionIdentifier = responseMap["id"]
			
		Date now = new Date()
		
		long nowTime = now.getTime()
		
		long thenTime = nowTime + timeout_ms
		
		Boolean timeout = false
		
		while(!timeout) {
			
			String result = getPrediction(predictionIdentifier)
		
			Map resultMap = parser.parseText(result)
				
			// check status
			
			String status = resultMap["status"]
			
			// status:
			// starting, processing, succeeded, failed, canceled
			
			if( status == "succeeded") { return result }
			
			// error
			if( status == "failed") { return result }
			
			// no result
			if(status == "canceled") { return result }
			
			if(status == "starting") {
				
				log.info("starting")
				
			}
			
			if(status == "processing") {
				
				log.info("processing")
				
			}
			
			// sleep half second
			Thread.sleep(500)
			
			Date reallyNow = new Date()
			
			long reallyNowTime = reallyNow.getTime()
			
			if(reallyNowTime >= thenTime) { timeout = true }
			
		}
		
		// TODO cleanup after timeout
		
		log.error("timeout")
		
		// timeout
		return null
	}
	

	// get list of predictions
	// GET https://api.replicate.com/v1/predictions
		
	// cancel prediction
	// POST https://api.replicate.com/v1/predictions/{prediction_id}/cancel

	// get model
	// GET https://api.replicate.com/v1/models/{model_owner}/{model_name}

	// get list of model versions
	// GET https://api.replicate.com/v1/models/{model_owner}/{model_name}/versions

	
	// get a model version
	// GET https://api.replicate.com/v1/models/{model_owner}/{model_name}/versions/{id}

	// get collection of models
	// GET https://api.replicate.com/v1/collections/{collection_slug}

	
	
}
