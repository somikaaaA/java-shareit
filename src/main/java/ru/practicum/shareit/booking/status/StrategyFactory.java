package ru.practicum.shareit.booking.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InvalidParameterForBooking;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class StrategyFactory {
    private Map<Status, Strategy> strategies;

    @Autowired
    public StrategyFactory(Set<Strategy> strategySet) {
        createStrategy(strategySet);
    }

    public Strategy findStrategy(Status strategyName) {
        Strategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            throw new InvalidParameterForBooking("Некорректный параметр запроса state " + strategyName);
        }
        return strategy;
    }

    private void createStrategy(Set<Strategy> strategySet) {
        strategies = new HashMap<Status, Strategy>();
        strategySet.forEach(
                strategy -> strategies.put(strategy.getStatusName(), strategy));
    }
}
