package matrix_consumer;

import cuda_matrix.cuda_matrix;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import java.util.stream.IntStream;
import javax.swing.plaf.synth.SynthSeparatorUI;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class matrix_consumer {

	private static final String TOPIC = "sstd1";
	private static final int NUM_THREADS = 1;

	// static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	
	
	public static void main(String[] args) throws Exception {

		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Properties props = new Properties();
		props.put("group.id", "super-group");
		props.put("zookeeper.connect", "163.152.174.73:2182");
		props.put("zk.connectiontimeout.ms", "1000000");
		props.put("zookeeper.session.timeout.ms", "1000000");	// 
		props.put("auto.commit.interval.ms", "6000");
		props.put("auto.offset.reset", "smallest");
		
		ConsumerConfig consumerConfig = new ConsumerConfig(props);
		ConsumerConnector consumer = Consumer.createJavaConsumerConnector(consumerConfig);
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(TOPIC, NUM_THREADS);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(TOPIC);
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

//		System.out.println("partial success!");
//		final cuda_matrix jcuda_matrix = new cuda_matrix(23);		

		for (final KafkaStream<byte[], byte[]> stream : streams) {   
		
			// MyFrame frame = new MyFrame(); 
			
				Runnable runnable = new Runnable() {
					public void run() {
						
					for (final MessageAndMetadata<byte[], byte[]> messageAndMetadata : stream) {
//						System.out.println("for start! 2 thread name : "+ Thread.currentThread().getName());				
						cuda_matrix jcuda_matrix = new cuda_matrix(40);		    //cuda
						
						
					
						byte[] test = messageAndMetadata.message();
						
			//			System.out.println(test.length);
						jcuda_matrix.prepare_cuda_memory(test);				//cuda
						
																
				
						// Mat data = new Mat(480, 640, CvType.CV_8UC3);
						// data.put(0, 0, test);
						// frame.setVisible(true);
						// frame.render(data);
	//					jcuda_matrix.cudaCleanUp();
						
						System.out.println("Processing complete! \n ------\n ------------\n");
						System.out.println("------");
					
			}
					}
				};
				executor.execute(runnable);
		}
	
		Thread.sleep(90000000);
		System.out.println("empty topic!!");
		consumer.shutdown();
		executor.shutdown();
	}
	
	//utf-8

}