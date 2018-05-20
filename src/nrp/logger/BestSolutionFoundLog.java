package nrp.logger;

class BestSolutionFoundLog
{
    private long timeFound;
    private int heuristicNumber;
    private double solutionValue;

    /**
     * @param timeFound
     * @param heuristicNumber
     * @param solutionValue
     */
    protected BestSolutionFoundLog( long timeFound, int heuristicNumber, double solutionValue )
    {
        this.timeFound = timeFound;
        this.heuristicNumber = heuristicNumber;
        this.solutionValue = solutionValue;
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