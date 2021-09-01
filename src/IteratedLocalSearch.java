import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IteratedLocalSearch {
	private Model model;
	private int n_exams;
	private int n_timeslots;
	private double optPenaltyLocal = Double.MAX_VALUE;
	private int[][] conflictMatrix;

	public IteratedLocalSearch(Model model) {
		this.model = model;
		this.n_exams = model.getExms().size();
		this.n_timeslots = model.getN_timeslots();
		this.conflictMatrix= model.getConflictMatrix();
	}
	
	// called method in crossover
	public Integer[] run(Integer[] chrom) {
		double currentPenalty;
		double newPenalty;
																			
		Integer[] optSolution = chrom;
		Integer[] newSolution;
		Integer[] currentSolution;

		currentSolution = chrom; // solution passed from crossover
		currentPenalty = model.computePenalty(currentSolution);
		optPenaltyLocal = currentPenalty;

		do {
			newSolution = generateNeigh(currentSolution); // generating neighborhood
			newPenalty = model.computePenalty(newSolution);
			
			//if the penalty from old solution to the new one has improved of at least a thousandth of the my optimal old solution, the new one is explored again  

			if ((currentPenalty - newPenalty) > 0.0005) {
				currentPenalty = newPenalty;
				currentSolution = newSolution.clone();

			} else {
				
				if (!isMinLocalYet(newSolution)) {
					model.addMinLoc(newSolution.clone());
				} else break;
			
				if (newPenalty < optPenaltyLocal) {
					optSolution = newSolution.clone();
					optPenaltyLocal = newPenalty;
					
					if(model.isNewOpt(optSolution)) {
						Thread current = Thread.currentThread();
						System.out.println("Found by " + current.getName());
					} 
					
				} else break;
				
				currentSolution = swapTimeslot(newSolution);
				currentPenalty = model.computePenalty(currentSolution);
				
			}
		} while (true);

		return optSolution;
	}

	/*
	 * Generating the neighborhood of a given chromosome
	 */
	public Integer[] generateNeigh(Integer[] chrom) {
		double bestPenalty = 0;
		double newPenalty;
		double actualPenalty;
		Integer[] bestSol = chrom.clone();
		
		for (int e = 0; e < this.n_exams; e++) { // for every exam
			Integer[] newSol = chrom.clone();
			actualPenalty = model.computePenaltyByExam(chrom, e); // computing weight-penalty for exam e

			for (int i = 1; i <= this.n_timeslots; i++) { // for every time-slot
				if (!model.areConflictual(i, e, newSol) && i != chrom[e]) { // checking if time-slot is feasible
					newSol[e] = i;
					newPenalty = model.computePenaltyByExam(newSol, e); //computing weight-penalty with the new exam

					// if the difference between the two penalty (the new one and the old one) is bigger than
					// the best previous solution
					if ((actualPenalty - newPenalty) > bestPenalty ) {
						bestPenalty = (actualPenalty - newPenalty);
						bestSol = newSol.clone();

					}
				}
			}
		}

		return bestSol;
	}
	
	/*
	 * Checking if a solution is a local minimum 
	 */
	public boolean isMinLocalYet(Integer[] solution) {
		List<Integer[]> minLoc = new ArrayList<>(model.getMinLoc());
		for (Integer[] ml: minLoc)
			if (Arrays.equals(ml, solution))
				return true;

		return false;
	}
	/*
	 * Computing the best swapping exams blocks between time-slots couples between all possible ones
	 */
	private Integer[] swapTimeslot(Integer[] sol) {
		Integer[] test;
		Integer[] bestSol = sol.clone();
		double bestPenalty = Double.MAX_VALUE;
		
		for(int timeS1 =1; timeS1<=this.n_timeslots; timeS1++) {
			List<String> exm1 = new ArrayList<>();
			
			for(int i =0; i<this.n_exams; i++) {
				if(sol[i]==timeS1)
					exm1.add(String.valueOf(i));
			}
			
			for(int timeS2 = 1; timeS2<=this.n_timeslots; timeS2++) {
	
				test = sol.clone();
				if(timeS2 != timeS1) {
					List<String> exm2 = new ArrayList<>();
					
					for(int i =0; i<this.n_exams; i++) {
						if(sol[i]==timeS2)
							exm2.add(String.valueOf(i));
					}
					
					List<String> exmSwap1 = new ArrayList<>();
					List<String> exmSwap2 = new ArrayList<>();
					
					for(String e : new ArrayList<>(exm1))
						for(String e2 : exm2)
							if(this.conflictMatrix[Integer.valueOf(e)][Integer.valueOf(e2)] > 0) {
								exmSwap1.add(e);
								break;
							}
					
					for(String e : new ArrayList<>(exm2))
						for(String e1 : exm1)
							if(this.conflictMatrix[Integer.valueOf(e)][Integer.valueOf(e1)] > 0 ) {
								exmSwap2.add(e);
								break;
							}
					
					for(String e : exmSwap1)
						test[Integer.valueOf(e)] = timeS2;
					
					for(String e : exmSwap2)
						test[Integer.valueOf(e)] = timeS1;	
					
					if(model.computePenalty(test)<bestPenalty) {
						bestSol = test.clone();
						bestPenalty = model.computePenalty(test);
					}
				}
			}
		}
		
		return bestSol;			
		
	}
	

}