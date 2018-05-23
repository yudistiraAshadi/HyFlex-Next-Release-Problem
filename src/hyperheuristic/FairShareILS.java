package hyperheuristic;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import java.util.Vector;

public class FairShareILS extends HyperHeuristic {
    ProblemDomain problem;
    double T = 0.5;
    int c_current = 0;
    int c_proposed = 1;
    int[] llhs_pert;
    int[] llhs_ls;
    int[] news;
    int[] durations;
    double[] evaluations;
    double e_proposed;
    double e_current;
    double e_run_best;
    double e_best;
    double mu_impr;
    int n_impr;
    long t_best;
    long t_run_start;
    long max_wait;
    long wait;

    public FairShareILS(long seed) {
        super(seed);
    }

    public FairShareILS(long seed, double T) {
        this(seed);
        this.T = T;
    }

    protected void solve(ProblemDomain problem) {
        this.setup(problem);
        this.init();
        while (!this.hasTimeExpired()) {
            long before = this.getElapsedTime();
            int option = this.select_option();
            this.apply_option(option);
            int[] arrn = this.durations;
            int n = option;
            arrn[n] = (int) ((long) arrn[n] + (this.getElapsedTime() - before + 1));
            if (!problem.compareSolutions(this.c_proposed, this.c_current) && this.accept()) {
                int[] arrn2 = this.news;
                int n2 = option;
                arrn2[n2] = arrn2[n2] + 1;
                this.e_current = this.e_proposed;
                problem.copySolution(this.c_proposed, this.c_current);
            }
            this.evaluations[option] = (1.0 + (double) this.news[option]) / (double) this.durations[option];
            if (!this.restart())
                continue;
            this.init();
        }
    }

    private void setup(ProblemDomain problem) {
        this.llhs_ls = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.LOCAL_SEARCH);
        int[] mut_llh = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.MUTATION);
        int[] rc_llh = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.RUIN_RECREATE);
        this.llhs_pert = new int[mut_llh.length + rc_llh.length];
        int i = 0;
        while (i < mut_llh.length) {
            this.llhs_pert[i] = mut_llh[i];
            ++i;
        }
        i = 0;
        while (i < rc_llh.length) {
            this.llhs_pert[i + mut_llh.length] = rc_llh[i];
            ++i;
        }
        this.problem = problem;
        this.e_best = Double.MAX_VALUE;
        this.max_wait = 1;
        this.t_best = 0;
    }

    private void init() {
        this.news = new int[this.llhs_pert.length + 1];
        this.durations = new int[this.llhs_pert.length + 1];
        this.evaluations = new double[this.llhs_pert.length + 1];
        int i = 0;
        while (i < this.evaluations.length) {
            this.evaluations[i] = Double.MAX_VALUE / (double) (this.llhs_pert.length + 1);
            ++i;
        }
        this.mu_impr = 0.0;
        this.n_impr = 0;
        this.problem.initialiseSolution(this.c_current);
        this.e_run_best = this.e_current = this.problem.getFunctionValue(this.c_current);
        this.wait = 0;
        this.t_run_start = this.getElapsedTime();
    }

    private int select_option() {
        double[] evaluations = this.evaluations;
        double norm = 0.0;
        int i = 0;
        while (i < evaluations.length) {
            norm += evaluations[i];
            ++i;
        }
        double p = this.rng.nextDouble() * norm;
        int selected = 0;
        double ac = evaluations[0];
        while (ac < p) {
            ac += evaluations[++selected];
        }
        return selected;
    }

    private void apply_option(int option) {
        if (option < this.llhs_pert.length) {
            this.e_proposed = this.problem.applyHeuristic(this.llhs_pert[option], this.c_current, this.c_proposed);
        } else {
            this.problem.initialiseSolution(this.c_proposed);
            this.e_proposed = this.problem.getFunctionValue(this.c_proposed);
        }
        this.hasTimeExpired();
        this.localsearch();
    }

    private void localsearch() {
        Vector<Integer> active = new Vector<Integer>();
        int i = 0;
        while (i < this.llhs_ls.length) {
            active.add(this.llhs_ls[i]);
            ++i;
        }
        while (!active.isEmpty()) {
            int index = this.rng.nextInt(active.size());
            double e_temp = this.problem.applyHeuristic(((Integer) active.get(index)).intValue(), this.c_proposed,
                    this.c_proposed);
            this.hasTimeExpired();
            if (e_temp < this.e_proposed) {
                this.e_proposed = e_temp;
                active.clear();
                int i2 = 0;
                while (i2 < this.llhs_ls.length) {
                    active.add(this.llhs_ls[i2]);
                    ++i2;
                }
                continue;
            }
            active.remove(index);
        }
    }

    private boolean accept() {
        if (this.e_proposed < this.e_current) {
            ++this.n_impr;
            this.mu_impr += (this.e_current - this.e_proposed - this.mu_impr) / (double) this.n_impr;
        }
        if (this.rng.nextDouble() < Math.exp((this.e_current - this.e_proposed) / (this.T * this.mu_impr))) {
            return true;
        }
        return false;
    }

    private boolean restart() {
        if (this.e_current < this.e_run_best) {
            this.e_run_best = this.e_current;
            this.max_wait = Math.max(this.wait, this.max_wait);
            this.wait = 0;
            if (this.e_run_best < this.e_best) {
                this.e_best = this.e_run_best;
                this.t_best = this.getElapsedTime() - this.t_run_start;
            } else if (this.e_run_best == this.e_best) {
                this.t_best = Math.min(this.getElapsedTime() - this.t_run_start, this.t_best);
            }
        } else {
            ++this.wait;
            double time_factor = (double) this.getTimeLimit() / (double) this.getElapsedTime();
            double patience = (double) this.max_wait * time_factor;
            if (this.max_wait != -1 && (double) this.wait > patience
                    && this.getTimeLimit() - this.getElapsedTime() >= this.t_best) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "FairShareILS(T:" + this.T + ")";
    }
}