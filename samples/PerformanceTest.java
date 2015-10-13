public class PerformanceTest {
	int loopCount;

	public PerformanceTest(int count){
		this.loopCount= count;
	}

	public static void main(String[] args){
		PerformanceTest pt= new PerformanceTest(1000000);
		pt.prepareLoop();
		pt.doLoop();
		pt.endLoop();
	}

	private void prepareLoop(){
		System.out.println("Prepare Loop");
		for (int i= 0; i < loopCount / 10; i++){
			Math.random();
		}
	}

	private void doLoop(){
		System.out.println("Starting Loop...");
		for (int i= 0; i < loopCount; i++){
			doTask();
		}
		System.out.println("Loop done.");
	}

	private void doTask(){
			Math.random();
	}
	
	private void endLoop(){
		for (int i= 0; i < loopCount / 10; i++){
			Math.random();
		}
		System.out.println("Loop Ended");
	}
}