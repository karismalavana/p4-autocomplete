import java.util.*;

public class HashListAutocomplete implements Autocompletor {

    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize;

    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
            throw new NullPointerException("One or more arguments null");
        }

        if (terms.length != weights.length) {
            throw new IllegalArgumentException("terms and weights are not the same length");
        }

        initialize(terms, weights);
    }

    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (prefix.length() > MAX_PREFIX) {
            prefix = prefix.substring(0, MAX_PREFIX);
        }

        if (myMap.keySet().contains(prefix)) {
            List<Term> all = myMap.get(prefix);
            return all.subList(0, Math.min(k, all.size()));
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<>();
        mySize = 0;

        for (int j = 0; j < terms.length; j++) {
            for (int i = 0; i <= MAX_PREFIX && i < terms[j].length() + 1; i++) {
                String pref = terms[j].substring(0, i);
                if (!myMap.keySet().contains(pref)) {
                    myMap.put(pref, new ArrayList<>());
                    mySize += BYTES_PER_CHAR * pref.length();
                }
                Term t = new Term(terms[j], weights[j]);
                myMap.get(pref).add(t);
                if (pref.equals(""))
                    mySize += BYTES_PER_CHAR * terms[j].length() + BYTES_PER_DOUBLE;
            }
        }

        for (String key : myMap.keySet()) {
            Collections.sort(myMap.get(key), Comparator.comparing(Term::getWeight).reversed());
        }
    }

    @Override
    public int sizeInBytes() {
        return mySize;
    }

    /*
     * public static void main(String[] args) {
     * String [] terms = {"automatically", "hexadecimal", "uncontrollable",
     * "uncontrollably", "uncontrollability", "thermodynamics"};
     * double [] weights = {4.0, 2.0, 5.0, 1.0, 3.0,6.0};
     * HashListAutocomplete bill = new HashListAutocomplete(terms, weights);
     * System.out.println(bill.topMatches("automatica", 1 ).toString());
     * }
     */
    // [(4.0,automatically), (2.0,hexadecimal), (5.0,uncontrollable),
    // (1.0,uncontrollably), (3.0,uncontrollability), (6.0,thermodynamics)]
}
