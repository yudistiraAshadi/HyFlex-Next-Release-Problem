package nrp.logger;

class HeuristicLog
{
    private long timeElapsed;
    private int heuristicNumber;

    /**
     * @param timeElapsed
     * @param heuristicNumber
     */
    protected HeuristicLog( long timeElapsed, int heuristicNumber )
    {
        this.timeElapsed = timeElapsed;
        this.heuristicNumber = heuristicNumber;
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