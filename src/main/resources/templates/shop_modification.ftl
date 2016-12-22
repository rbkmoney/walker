h5. Операция: Редактирование магазина
* Созданы счета:";
description += "\n в валюте: " + accounts.getCurrency().getSymbolicCode();
description += "\n освновной счет: " + accounts.getGeneral();
description += "\n гарантийный счет: " + accounts.getGuarantee();
} else if (shopModification.isSetUpdate()) {
ShopUpdate update = shopModification.getUpdate();
description += "\n Изменен магазин : " + Optional.ofNullable(update.getDetails()).map(ShopDetails::getName).orElse("-");
description += "\n Описание : " + Optional.ofNullable(update.getDetails()).map(ShopDetails::getDescription).orElse("-");
description += "\n Местоположение : " + Optional.ofNullable(update.getDetails()).map(ShopDetails::getLocation).map(ShopLocation::getUrl).orElse("-");
description += "\n Категория : " + Optional.ofNullable(update.getCategory()).map(CategoryRef::getId).orElse(0);
} else {
description += "\n " + shopModification.getFieldValue().toString();
}
return description;