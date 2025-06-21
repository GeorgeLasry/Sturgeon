class Result implements Comparable<Result>{
    final double score;
    final Key key;
    final WheelFunctionSet wheelFunctionSet;

    Result(double score, Key key) {
        this(score, key, null);
    }
    Result(double score, Key key, WheelFunctionSet wheelFunctionSet) {
        this.score = score;
        this.key = new Key(key);
        this.wheelFunctionSet = wheelFunctionSet == null ? null : new WheelFunctionSet(wheelFunctionSet);
    }

    @Override
    public int compareTo(Result o) {
        return Double.compare(score, o.score);
    }
}
