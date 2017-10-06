package lambda.client;

import java.nio.charset.Charset;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

/**
 * Example of AWS Lambda client
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class ExampleLambdaClient {

  public static void main(String[] args) {

    ExampleLambdaClient client = new ExampleLambdaClient();
    client.invokeLambdaFunction();

  }

  private void invokeLambdaFunction() {

    final String AWS_ACCESS_KEY_ID = "[YOUR_AWS_ACCESS_KEY_ID]";
    final String AWS_SECRET_ACCESS_KEY = "[YOUR_SECRET_ACCESS_KEY]";

    AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);

    // ARN
    String functionName = "[YOUR_FUNCTION_NAME_OR_ARN]";

    String inputJSON = "{\"firstName\":\"john\",\"lastName\": \"doe\"}";

    InvokeRequest lmbRequest = new InvokeRequest()
        .withFunctionName(functionName)
        .withPayload(inputJSON);

    lmbRequest.setInvocationType(InvocationType.RequestResponse);

    AWSLambda lambda = AWSLambdaClientBuilder.standard()
        .withRegion(Regions.AP_NORTHEAST_1)
        .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

    InvokeResult lmbResult = lambda.invoke(lmbRequest);

    String resultJSON = new String(lmbResult.getPayload().array(), Charset.forName("UTF-8"));

    System.out.println(resultJSON);

  }
}