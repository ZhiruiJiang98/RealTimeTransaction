package dev.codescreen.module;
import com.google.inject.AbstractModule;
import dev.codescreen.library.storage.AccountStorageManager;
import dev.codescreen.library.storage.TransactionStorageManager;

public class TransactionServiceModule extends AbstractModule{
   @Override
    protected void configure() {
       install(new MySqlClientModule());
       bind(AccountStorageManager.class).toInstance(new AccountStorageManager());
       bind(TransactionStorageManager.class).toInstance(new TransactionStorageManager());
   }
}
