package com.github.johantiden.dwarfactory.game.world;

import com.badlogic.gdx.utils.Predicate;
import com.github.johantiden.dwarfactory.game.TileCoordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class Automata {

    private final List<Rule> rules = new ArrayList<>();

    private static final Boolean _ = true;
    private static final Boolean X = false;

    private final Map<TileCoordinate, TileType> map;

    public Automata(Map<TileCoordinate, TileType> map) {
        this.map = map;

        put(new RuleKey(
                _, _, _,
                _, X, _,
                _, _, _
        ), _);

        put(new RuleKey(
                X, X, X,
                X, _, X,
                X, X, X
        ), X);

        put(new RuleKey(
                _, X, X,
                X, _, X,
                X, X, X
        ), X);

        put(new RuleKey(
                X, X, X,
                _, _, X,
                X, X, X
        ), X);

        put(new RuleKey(
                X, _, X,
                _, _, X,
                X, X, X
        ), X);

        put(new RuleKey(
                _, X, X,
                _, _, X,
                X, X, X
        ), X);
        put(new RuleKey(
                _, X, _,
                X, _, X,
                X, X, X
        ), X);
    }

    private void put(RuleKey ruleKey, Boolean output) {
        rules.add(new Rule(position -> {
            TileType oldValue = map.get(position);
            RuleKey key = new RuleKey(
                    isMatching(position.north().west(), oldValue),
                    isMatching(position.north(), oldValue),
                    isMatching(position.north().east(), oldValue),
                    isMatching(position.west(), oldValue),
                    true,
                    isMatching(position.east(), oldValue),
                    isMatching(position.south().west(), oldValue),
                    isMatching(position.south(), oldValue),
                    isMatching(position.south().east(), oldValue)
            );

            return matches(key, ruleKey);
        }, position -> {
            if (output == null) {
                return null;
            }
            TileType tileType = map.get(position);
            return tileType.tileFunctionalType == TileFunctionalType.GRASS ? TileFunctionalType.WATER : TileFunctionalType.GRASS;
        } ));
    }

    private boolean matches(RuleKey a, RuleKey b) {
        List<BiPredicate<RuleKey, RuleKey>> tests = new ArrayList<>();

        tests.add(RuleKey::equals);

        tests.add((c, d) -> c.flip().equals(d));
        tests.add((c, d) -> c.rotate(1).equals(d));
        tests.add((c, d) -> c.rotate(2).equals(d));
        tests.add((c, d) -> c.rotate(3).equals(d));

        return tests.stream()
                .anyMatch(test -> test.test(a, b));
    }


    public void iterate() {
        map.keySet().forEach(key -> map.put(key, iterateOne(key)));
    }

    private TileType iterateOne(TileCoordinate position) {

        return rules.stream()
                .filter(rule -> rule.predicate.evaluate(position))
                .findAny()
                .map(rule -> {
                    return TileType.randomMatchingFunctional(rule.output.apply(position));
                })
                .orElseGet(() -> {
                    System.out.println("Missing rule!");
                    return map.get(position);
                });
    }

    private boolean isMatching(TileCoordinate tileCoordinate, TileType oldValue) {
        TileType tileType = map.get(tileCoordinate);

        if (tileType == null && oldValue == null) {
            return true;
        }

        if (tileType == null || oldValue == null) {
            return false;
        }

        return tileType.tileFunctionalType == oldValue.tileFunctionalType;
    }


    private static class RuleKey {
        private final Boolean nw;
        private final Boolean n;
        private final Boolean ne;

        private final Boolean w;
        private final Boolean c;
        private final Boolean e;

        private final Boolean sw;
        private final Boolean s;
        private final Boolean se;

        private RuleKey(Boolean nw, Boolean n, Boolean ne, Boolean w, Boolean c, Boolean e, Boolean sw, Boolean s, Boolean se) {
            this.nw = nw;
            this.n = n;
            this.ne = ne;
            this.w = w;
            this.c = c;
            this.e = e;
            this.sw = sw;
            this.s = s;
            this.se = se;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            RuleKey ruleKey = (RuleKey) o;
            return Objects.equals(nw, ruleKey.nw) &&
                    Objects.equals(n, ruleKey.n) &&
                    Objects.equals(ne, ruleKey.ne) &&
                    Objects.equals(w, ruleKey.w) &&
                    Objects.equals(c, ruleKey.c) &&
                    Objects.equals(e, ruleKey.e) &&
                    Objects.equals(sw, ruleKey.sw) &&
                    Objects.equals(s, ruleKey.s) &&
                    Objects.equals(se, ruleKey.se);
        }

        @Override
        public int hashCode() {

            return Objects.hash(nw, n, ne, w, c, e, sw, s, se);
        }

        public RuleKey invert() {
            return new RuleKey(
                    invertt(nw), invertt(n), invertt(ne), invertt(w), invertt(c), invertt(e), invertt(sw), invertt(s), invertt(se)
            );
        }

        public RuleKey flip() {
            return new RuleKey(
                    ne, n, nw,
                    e, c, w,
                    se, s, sw
            );
        }

        public RuleKey rotate(int times) {
            RuleKey rotated = new RuleKey(
                    sw, w, nw,
                    s, c, n,
                    se, e, ne
            );
            if (times == 1) {
                return rotated;
            } else {
                return rotated.rotate(times - 1);
            }
        }

        @Override
        public String toString() {
            return ""+
                    c(nw) + c(n) + c(ne) + '-' +
                    c(w) + c(c) + c(e) + '-' +
                    c(sw) + c(s) + c(se);
        }
    }

    private static Boolean invertt(Boolean bool) {
        return bool != null ? !bool : null;
    }

    private static char c(Boolean val) {
        if (val == null) {
            return '.';
        }
        return val == _ ? '_' : 'X';
    }

    private static class Rule {
        private final Predicate<TileCoordinate> predicate;
        private final Function<TileCoordinate, TileFunctionalType> output;

        private Rule(Predicate<TileCoordinate> predicate, Function<TileCoordinate, TileFunctionalType> output) {
            this.predicate = predicate;
            this.output = output;
        }
    }
}
