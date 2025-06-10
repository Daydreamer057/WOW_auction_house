package controller;

import dto.SelectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.CurrencyService;
import service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ItemService itemService;

    @GetMapping("/item-profit")
    public List<SelectDTO> getItemProfit() {
        return itemService.calculateProfitForItem();
    }
//     populate the select list with realms sorted by profit
//    @GetMapping("/select")
//    public ResponseEntity<SelectDTO> getItemsList() {
//        try {
//            return ResponseEntity.ok(currencyService.getAll());
//        } catch (NoSuchElementException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }


}
