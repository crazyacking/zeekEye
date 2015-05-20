import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import org.apache.http.concurrent.FutureCallback;
import sun.net.www.http.HttpClient;
package httpcomponents.*;import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by crazyacking on 2015/5/10.
 */


/**
 * This example demonstrates how the he HttpClient fluent API can be used to execute multiple
 * requests asynchronously using background threads.
 */
public class FluentAsync {

    public static void main(String[] args)throws Exception {
        // Use pool of two threads
        ExecutorService threadpool = Executors.newFixedThreadPool(2);
        Async async = Async.newInstance().use(threadpool);

        Request[] requests = new Request[] {
                Request.Get("http://www.google.com/"),
                Request.Get("http://www.yahoo.com/"),
                Request.Get("http://www.apache.com/"),
                Request.Get("http://www.apple.com/")
        };


        Queue<Future<Content>> queue = new LinkedList<Future<Content>>();
        // Execute requests asynchronously
        for (final Request request: requests) {
            Future<Content> future = async.execute(request, new FutureCallback<Content>() {

                @Override
                public void failed(final Exception ex) {
                    System.out.println(ex.getMessage() + ": " + request);
                }

                @Override
                public void completed(final Content content) {
                    System.out.println("Request completed: " + request);
                }

                @Override
                public void cancelled() {
                }

            });
            queue.add(future);
        }

        while(!queue.isEmpty()) {
            Future<Content> future = queue.remove();
            try {
                future.get();
            } catch (ExecutionException ex) {
            }
        }
        System.out.println("Done");
        threadpool.shutdown();
    }

}
