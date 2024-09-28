package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.DbSpendingExtension;
import guru.qa.niffler.jupiter.extension.SpendingExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(DbSpendingExtension.class)
public @interface DbSpending {
  String username();

  String category();

  String description();

  double amount();
}
