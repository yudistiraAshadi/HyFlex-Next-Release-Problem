package nrp.logger;

class BestSolutionFoundLog
{
    private int logApplyHeuristicIterationCounter;
    private long timeFound;
    private int heuristicNumber;
    private double solutionValue;

    /**
     * @param logApplyHeuristicIterationCounter
     * @param timeFound
     * @param heuristicNumber
     * @param solutionValue
     */
    protected BestSolutionFoundLog( int logApplyHeuristicIterationCounter, long timeFound,
            int heuristicNumber, double solutionValue )
    {
        this.logApplyHeuristicIterationCounter = logApplyHeuristicIterationCounter;
        this.timeFound = timeFound;
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
     * @return the timeFound
     */
    protected long getTimeFound()
    {
        return timeFound;
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