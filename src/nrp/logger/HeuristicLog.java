package nrp.logger;

class HeuristicLog
{
    private int logApplyHeuristicIterationCounter;
    private long timeElapsed;
    private int heuristicNumber;
    private double solutionValue;

    /**
     * @param logApplyHeuristicIterationCounter
     * @param timeElapsed
     * @param heuristicNumber
     * @param solutionValue
     */
    protected HeuristicLog( int logApplyHeuristicIterationCounter, long timeElapsed,
            int heuristicNumber, double solutionValue )
    {
        this.logApplyHeuristicIterationCounter = logApplyHeuristicIterationCounter;
        this.timeElapsed = timeElapsed;
        this.heuristicNumber = heuristicNumber;
        this.solutionValue = solutionValue;
    }

    /**
     * @return the logApplyHeuristicIterationCounter
     */
    protected int getLogApplyHeuristicIterationCounter()
    {
        return logApplyHeuristicIterationCounter;
    }

    /**
     * @return the timeElapsed
     */
    protected long getTimeElapsed()
    {
        return timeElapsed;
    }

    /**
     * @return the heuristicNumber
     */
    protected int getHeuristicNumber()
    {
        return heuristicNumber;
    }

    /**
     * @return the solutionValue
     */
    protected double getSolutionValue()
    {
        return solutionValue;
    }
}