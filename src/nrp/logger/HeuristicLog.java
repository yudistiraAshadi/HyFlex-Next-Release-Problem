package nrp.logger;

class HeuristicLog
{
    private int logApplyHeuristicIterationCounter;
    private long timeElapsed;
    private int heuristicNumber;

    /**
     * @param logApplyHeuristicIterationCounter
     * @param timeElapsed
     * @param heuristicNumber
     */
    protected HeuristicLog( int logApplyHeuristicIterationCounter, long timeElapsed,
            int heuristicNumber )
    {
        this.logApplyHeuristicIterationCounter = logApplyHeuristicIterationCounter;
        this.timeElapsed = timeElapsed;
        this.heuristicNumber = heuristicNumber;
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
}