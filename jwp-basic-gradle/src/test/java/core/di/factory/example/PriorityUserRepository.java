package core.di.factory.example;

import core.annotation.Order;
import core.annotation.Repository;

@Order(1)
@Repository
public class PriorityUserRepository implements UserRepository{
}
