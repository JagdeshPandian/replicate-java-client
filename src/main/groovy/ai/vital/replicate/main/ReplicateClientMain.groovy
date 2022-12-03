package ai.vital.replicate.main

import ai.vital.replicate.api.ReplicateJavaClient
import ai.vital.replicate.api.ReplicateRequest
import ai.vital.replicate.model.OpenJourneyModel
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.util.stream.Collectors
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.log4j.BasicConfigurator

class ReplicateClientMain extends groovy.lang.Script {

	String apiKey 
	
	static void main(args) {
		
		// sets up logging to defaults
		BasicConfigurator.configure()
		
		ReplicateClientMain app = new ReplicateClientMain()
		
		app.run()
		
	}
	
	public Object run() {
		
		println "Replicate Client Main"
		
		String configFile = "./config/replicateClient.conf"
		
		Config conf = ConfigFactory.parseFileAnySyntax(new File(configFile))
		
		apiKey = conf.getString("apiKey")
		
		// Test with Open Journey
			
		OpenJourneyModel modelClass = new OpenJourneyModel()
		
		String modelName = modelClass.getModelName()
		
		String modelVersion = modelClass.getModelVersion()
		
		ReplicateJavaClient modelClient = new ReplicateJavaClient(apiKey, modelName, modelVersion)
		
		String prompt = "mdjrny-v4 style portrait of female dragon, intricate, elegant, highly detailed, digital painting, artstation, concept art, smooth, sharp focus, illustration, art by artgerm and greg rutkowski and alphonse mucha, 8k"
		
		Integer width = 512
		
		Integer height = 512
		
		Integer numOutputs = 1
		
		Integer numInferenceSteps = 50
		
		Double guidanceScale = 7.0
		
		Integer seed = null
		
		// ReplicaterRequest is returned so we keep model name, version, and request in one object
		
		ReplicateRequest request = modelClass.generatePredictionRequest(prompt, width, height, numOutputs, numInferenceSteps, guidanceScale)
		
		String requestJSON = request.requestJSON
		
		String responseJSON = modelClient.generatePredictionPoll(requestJSON, 60_000)
		
		String prettyJSON = JsonOutput.prettyPrint(responseJSON)
		
		println "Output:\n" + prettyJSON
		
		JsonSlurper parser = new JsonSlurper()
		
		Map responseMap = parser.parseText(prettyJSON)
		
		List<String> outputList = responseMap["output"]
		
		int count = 0
		
		for(f in outputList) {
			
			count++
			
			CloseableHttpClient httpclient = HttpClients.createDefault()
			
			HttpGet httpget = new HttpGet( f )
			
			HttpResponse httpresponse = httpclient.execute(httpget)
			
			InputStream inputStream = httpresponse.getEntity().getContent()
			
			byte[] buff = new byte[64*1024]
			
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
			
			byte[] fileBytes = null
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream()
			
			int n = 0
			
			while ((n = bufferedInputStream.read(buff)) >= 0) {
				baos.write(buff, 0, n)
			}
			
			fileBytes =  baos.toByteArray()
			
			String fileName = "./outputImages/image${count}.png"
					
			File imageFile = new File(fileName)
			
			imageFile.bytes = fileBytes
			
		}
		
		
	}
	
	
}
