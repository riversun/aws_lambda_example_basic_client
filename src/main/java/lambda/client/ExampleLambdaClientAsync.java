package lambda.client;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

/**
 * Example of AWS Lambda async client
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class ExampleLambdaClientAsync {

  public static void main(String[] args) {

    ExampleLambdaClientAsync client = new ExampleLambdaClientAsync();
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

    AWSLambdaAsync lambdaAsync = AWSLambdaAsyncClientBuilder.standard()
        .withRegion(Regions.AP_NORTHEAST_1)
        .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

    final CountDownLatch latch = new CountDownLatch(1);

    final Future<InvokeResult> future = lambdaAsync.invokeAsync(lmbRequest, new AsyncHandler<InvokeRequest, InvokeResult>() {

      public void onSuccess(InvokeRequest req, InvokeResult res) {
        System.out.println("#onSuccess");
        ByteBuffer payload = res.getPayload();
        System.out.println(new String(payload.array()));

        // release waiting thread
        latch.countDown();
      }

      public void onError(Exception e) {
        System.err.println("#onError");
        e.printStackTrace();

        // release waiting thread
        latch.countDown();
      }
    });

    try {

      future.get();

      // wait current thread
      latch.await();

      // shutdown ExecutorService
      lambdaAsync.shutdown();

    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }

}