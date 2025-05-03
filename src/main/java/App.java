import dao.CurrencyDAO;
import dao.ItemDAO;
import dao.RealmDAO;
import entity.Currency;
import entity.Item;
import entity.Realm;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // DAOs
        ItemDAO itemDAO = new ItemDAO();
        RealmDAO realmDAO = new RealmDAO();
        CurrencyDAO currencyDAO = new CurrencyDAO();

        // INSERT: Item
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Sword of Valor");
        itemDAO.save(item1);

        // INSERT: Realm
        Realm realm1 = new Realm();
        realm1.setId(100);
        realm1.setName("Stormrage");
        realmDAO.save(realm1);

        // INSERT: Currency
        Currency currency = new Currency();
        currency.setItem(item1);
        currency.setRealm(realm1);
        currency.setCost(new BigDecimal("1299.99"));
        currencyDAO.save(currency);

        // QUERY: All Items
        List<Item> items = itemDAO.findAll();
        System.out.println("\n--- All Items ---");
        for (Item i : items) {
            System.out.printf("Item ID: %d | Name: %s%n", i.getId(), i.getName());
        }

        // QUERY: All Realms
        List<Realm> realms = realmDAO.findAll();
        System.out.println("\n--- All Realms ---");
        for (Realm r : realms) {
            System.out.printf("Realm ID: %d | Name: %s%n", r.getId(), r.getName());
        }

        // QUERY: All Currency
        List<Currency> currencies = currencyDAO.findAll();
        System.out.println("\n--- All Currency ---");
        for (Currency c : currencies) {
            System.out.printf("Item: %s | Realm: %s | Cost: %s%n",
                    c.getItem().getName(), c.getRealm().getName(), c.getCost().toPlainString());
        }

        // Shutdown Hibernate
        HibernateUtil.shutdown();
    }
}
