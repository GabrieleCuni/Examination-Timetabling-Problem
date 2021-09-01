public class Main {

	public static void main(String[] args) {
		long tlim = 0;
		long timeStart = System.currentTimeMillis();
		String instance;

		if (args.length != 3) {
			System.out.println("Err: Number of parameters");
			System.exit(1);
		}

		instance = args[0];

		if (!args[1].equalsIgnoreCase("-t")) {
			System.out.println("Err: Second parameter must be -t");
			System.exit(1);
		}

		try {
			tlim = Long.valueOf(args[2]);
		} catch (NumberFormatException e) {
			System.out.println("Err: Invalid timeout. It must be an integer number.");
			System.exit(1);
		}

		Model model = new Model(timeStart);

		// Read files
		model.loadIstance("Instances/" + instance);
		model.run();

		while((System.currentTimeMillis()-timeStart)/1000 <= tlim) {}
		if (!model.old_flag) 
			System.out.print("****Old solution was better****\n \nThis run:");
		
		System.out.print("\nBest penalty: " + model.getOptPenalty()+"\n");
		System.exit(1);
	}

}