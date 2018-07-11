package control;

import model.data.Ingredients;
import model.data.Item;
import model.data.Meal;
import model.managers.MenuItemManager;

/**
 * This class will convert a item to a specific format of String or vice versa.
 * The string format can be used to convert back the the item later one.
 */
public class ItemConverter {
    private MenuItemManager menuItemManager;
    public ItemConverter(){
        menuItemManager = new MenuItemManager();
    }

    /**
     * convert the data given into an Item object. the Item info should be in the format of
     * "name\n \t (\n \t+(addition)) ... (\n \t+(addition)) ... (\n \t*(additionalRequest))"
     * @param orderNumber - the order number of the item
     * @param tableNumber - the table number of the item
     * @param itemNumber - the item umber
     * @param itemInfo - the item information
     * @return Item that is being converted from the string.
     */
    public Item stringToItem(int orderNumber, int tableNumber, int itemNumber, String itemInfo){
        String[] infos = itemInfo.split("\n");
        for (int i = 0; i < infos.length; i++){
            infos[i] = infos[i].trim();
        }
        Meal base = menuItemManager.getMealItem(infos[0]);
        Meal item = new Meal(base, itemNumber, orderNumber, tableNumber);
        for(int i = 1; i < infos.length; i++){
            String text = infos[i];
            if (text.contains("-")){
                item.getIngredients().addSubtraction(text.replace("-", ""));
            }
            else if(text.contains("+")){
                item.getIngredients().addAddition(text.replace("+", ""));
            }else if(text.contains("*")){
                item.getItemInfo().setAdditionalRequest(text.replace("*", ""));
            }
        }
        return item;
    }

    /**
     * Create an string that contains the item information with the
     * specific format of
     * "name\n \t (\n \t+(addition)) ... (\n \t+(addition)) ... (\n \t*(additionalRequest))"
     * @return String converted from the item.
     */
    public String itemToInfoString(Item item){
        String info = item.getName();
        Ingredients ingredients = item.getIngredients();
        for(String addition : ingredients.getAddition().keySet()){
            int quantity = ingredients.getAddition().get(addition);
            int baseQuantity = ingredients.getBaseIngredient().get(addition);
            do{
                quantity -= baseQuantity;
                info = info + "\n \t +" + addition;
            }while (quantity > baseQuantity);
        }
        for(String subtraction:ingredients.getSubtraction().keySet()){
            info = info + "\n \t -" + subtraction;
        }
        String additionalRequest = item.getItemInfo().getAdditionalRequest();
        info = additionalRequest.isEmpty() ? info : info + "\n \t *" + additionalRequest;
        return info;
    }
}
