package pro.network.unniss.product;

import java.io.Serializable;

public class ProductListBean implements Serializable {
    public static final String TABLE_NAME = "unniss";
    public static final String TABLE_NAME_WISH = "unnissWish";
    public static final String COLUMN_PRO_ID = "proid";
    public static final String COLUMN_ID = "id";
    public static final String USER_ID = "userId";
    public static final String COLUMN_CART = "cart";
    public static final String COLUMN_WISH = "wish";
    public static final String COLUMN_RQTY_TYPE = "rqtyType";
    public static final String COLUMN_BRAND = "brand";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ROM = "rom";
    public static final String COLUMN_RQTY = "rqty";
    public static final String COLUMN_RAM = "ram";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_QTY = "qty";
    public static final String COLUMN_STOCKUPDATE = "stock_update";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_FABRIC = "fabric";
    public static final String COLUMN_BESTSELLING = "bestselling";
    public static final String COLUMN_PRICEDROP = "pricedrop";
    public static final String COLUMN_IDEAL = "ideal";
    public static final String COLUMN_OCCASION = "occasion";
    public static final String COLUMN_FIT = "fit";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_CLOSURE = "closure";
    public static final String COLUMN_POCKET = "pocket";
    public static final String COLUMN_PATTERN = "pattern";
    public static final String COLUMN_RISE = "rise";
    public static final String COLUMN_ORIGIN = "origin";
    public static final String COLUMN_VARIATION = "variation";
    public static final String COLUMN_VARIATIONID = "variationId";


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PRO_ID + " proid,"
                    + USER_ID + " userId,"
                    + COLUMN_CART + " cart,"
                    + COLUMN_BRAND + " brand,"
                    + COLUMN_NAME + " name,"
                    + COLUMN_RQTY + " rqty,"
                    + COLUMN_ROM + " rom,"
                    + COLUMN_RAM + " ram,"
                    + COLUMN_PRICE + " price,"
                    + COLUMN_MODEL + " model,"
                    + COLUMN_IMAGE + " image,"
                    + COLUMN_DESCRIPTION + " description,"
                    + COLUMN_QTY + " qty,"
                    + COLUMN_STOCKUPDATE + " stock_update,"
                    + COLUMN_CATEGORY + " category,"
                    + COLUMN_FABRIC + " fabric,"
                    + COLUMN_BESTSELLING + " bestselling,"
                    + COLUMN_PRICEDROP + " pricedrop,"
                    + COLUMN_IDEAL + " ideal,"
                    + COLUMN_OCCASION + " occasion,"
                    + COLUMN_FIT + " fit,"
                    + COLUMN_COLOR + " color,"
                    + COLUMN_SIZE + " size,"
                    + COLUMN_CLOSURE + " closure,"
                    + COLUMN_POCKET + " pocket,"
                    + COLUMN_PATTERN + " pattern,"
                    + COLUMN_RISE + " rise,"
                    + COLUMN_ORIGIN + " origin,"
                    + COLUMN_RQTY_TYPE + " rqtyType,"
                    + COLUMN_VARIATION + " variation,"
                    + COLUMN_VARIATIONID + " variationId"

                    + ")";
    public static final String CREATE_TABLE_WISH =
            "CREATE TABLE " + TABLE_NAME_WISH + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PRO_ID + " proid,"
                    + USER_ID + " userId,"
                    + COLUMN_WISH + " wish,"
                    + COLUMN_BRAND + " brand,"
                    + COLUMN_NAME + " name,"
                    + COLUMN_RQTY + " rqty,"
                    + COLUMN_ROM + " rom,"
                    + COLUMN_RAM + " ram,"
                    + COLUMN_PRICE + " price,"
                    + COLUMN_MODEL + " model,"
                    + COLUMN_IMAGE + " image,"
                    + COLUMN_DESCRIPTION + " description,"
                    + COLUMN_QTY + " qty,"
                    + COLUMN_FABRIC + " fabric,"
                    + COLUMN_BESTSELLING + " bestselling,"
                    + COLUMN_PRICEDROP + " pricedrop,"
                    + COLUMN_IDEAL + " ideal,"
                    + COLUMN_OCCASION + " occasion,"
                    + COLUMN_FIT + " fit,"
                    + COLUMN_COLOR + " color,"
                    + COLUMN_SIZE + " size,"
                    + COLUMN_CLOSURE + " closure,"
                    + COLUMN_POCKET + " pocket,"
                    + COLUMN_PATTERN + " pattern,"
                    + COLUMN_RISE + " rise,"
                    + COLUMN_ORIGIN + " origin,"
                    + COLUMN_STOCKUPDATE + " stock_update,"
                    + COLUMN_RQTY_TYPE + " rqtyType,"
                    + COLUMN_VARIATION + " variation,"
                    + COLUMN_VARIATIONID + " variationId"

                    + ")";
    public String id;

    public String userId;
    public String cart;
    public String wish;
    public String brand;
    public String name;
    public String rom;
    public String ram;
    public String price;
    public String model;
    public String image;
    public String rqty;
    public String description;
    public String qty;
    public String stock_update;
    public String category;
    public String fabric;
    public String bestselling;
    public String pricedrop;
    public String ideal;
    public String occasion;
    public String fit;
    public String color;
    public String size;
    public String closure;
    public String pocket;
    public String pattern;
    public String rise;
    public String origin;
    public String rqtyType;
    public String expressStock;
    public String variation;
    public String variationId;

    public ProductListBean() {
    }

    public ProductListBean(String userId, String cart, String wish, String brand, String rqty, String name, String rom, String ram,
                           String price, String model, String image, String description, String fabric, String bestselling, String pricedrop, String ideal,
                           String occasion,
                           String fit,
                           String color,
                           String size,
                           String closure,
                           String pocket,
                           String pattern,
                           String rise,
                           String origin, String qty, String stock_update,
                           String category, String rqtyType,String variation,String variationId) {
        this.userId = userId;
        this.cart = cart;
        this.wish = wish;
        this.brand = brand;
        this.name = name;
        this.rom = rom;
        this.ram = ram;
        this.price = price;
        this.model = model;
        this.rqty = rqty;
        this.image = image;
        this.fabric = fabric;
        this.bestselling = bestselling;
        this.pricedrop = pricedrop;
        this.ideal = ideal;
        this.occasion = occasion;
        this.fit = fit;
        this.color = color;
        this.size = size;
        this.closure = closure;
        this.pocket = pocket;
        this.pattern = pattern;
        this.rise = rise;
        this.origin = origin;
        this.description = description;
        this.qty = qty;
        this.stock_update = stock_update;
        this.category = category;
        this.rqtyType = rqtyType;
        this.variation = variation;
        this.variationId = variationId;
    }


    public ProductListBean(String userId, String cart, String rqty, String brand, String name, String rom, String ram, String price,
                           String model, String image, String description, String qty, String stock_update, String fabric, String bestselling, String pricedrop, String ideal,
                           String occasion,
                           String fit,
                           String color,
                           String size,
                           String closure,
                           String pocket,
                           String pattern,
                           String rise,
                           String origin, String category,String variation,String variationId) {
        this.userId = userId;
        this.cart = cart;
        this.brand = brand;
        this.name = name;
        this.rom = rom;
        this.rqty = rqty;
        this.ram = ram;
        this.price = price;
        this.model = model;
        this.image = image;
        this.fabric = fabric;
        this.bestselling = bestselling;
        this.pricedrop = pricedrop;
        this.ideal = ideal;
        this.occasion = occasion;
        this.fit = fit;
        this.color = color;
        this.size = size;
        this.closure = closure;
        this.pocket = pocket;
        this.pattern = pattern;
        this.rise = rise;
        this.origin = origin;
        this.description = description;
        this.qty = qty;
        this.stock_update = stock_update;
        this.category = category;
        this.variation = variation;
        this.variationId = variationId;
    }

    public static String getColumnRqty() {
        return COLUMN_RQTY;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColumnId() {
        return COLUMN_ID;
    }

    public static String getUserId() {
        return USER_ID;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static String getColumnCart() {
        return COLUMN_CART;
    }

    public static String getColumnBrand() {
        return COLUMN_BRAND;
    }

    public static String getColumnName() {
        return COLUMN_NAME;
    }

    public static String getColumnRom() {
        return COLUMN_ROM;
    }

    public static String getColumnRam() {
        return COLUMN_RAM;
    }

    public static String getColumnPrice() {
        return COLUMN_PRICE;
    }

    public static String getColumnModel() {
        return COLUMN_MODEL;
    }

    public static String getColumnImage() {
        return COLUMN_IMAGE;
    }

    public static String getCreateTable() {
        return CREATE_TABLE;
    }

    public static String getColumnProId() {
        return COLUMN_PRO_ID;
    }

    public static String getColumnDescription() {
        return COLUMN_DESCRIPTION;
    }

    public static String getColumnQty() {
        return COLUMN_QTY;
    }

    public static String getColumnStockupdate() {
        return COLUMN_STOCKUPDATE;
    }

    public static String getColumnCategory() {
        return COLUMN_CATEGORY;
    }

    public static String getTableNameWish() {
        return TABLE_NAME_WISH;
    }

    public static String getColumnWish() {
        return COLUMN_WISH;
    }

    public static String getColumnRqtyType() {
        return COLUMN_RQTY_TYPE;
    }

    public static String getColumnFabric() {
        return COLUMN_FABRIC;
    }

    public static String getColumnIdeal() {
        return COLUMN_IDEAL;
    }

    public static String getColumnOccasion() {
        return COLUMN_OCCASION;
    }

    public static String getColumnFit() {
        return COLUMN_FIT;
    }

    public static String getColumnColor() {
        return COLUMN_COLOR;
    }

    public static String getColumnSize() {
        return COLUMN_SIZE;
    }

    public static String getColumnClosure() {
        return COLUMN_CLOSURE;
    }

    public static String getColumnPocket() {
        return COLUMN_POCKET;
    }

    public static String getColumnPattern() {
        return COLUMN_PATTERN;
    }

    public static String getColumnRise() {
        return COLUMN_RISE;
    }

    public static String getColumnOrigin() {
        return COLUMN_ORIGIN;
    }

    public static String getCreateTableWish() {
        return CREATE_TABLE_WISH;
    }

    public static String getColumnBestselling() {
        return COLUMN_BESTSELLING;
    }

    public static String getColumnPricedrop() {
        return COLUMN_PRICEDROP;
    }

    public String getRqtyType() {
        return rqtyType;
    }

    public void setRqtyType(String rqtyType) {
        this.rqtyType = rqtyType;
    }

    public String getRqty() {
        return rqty;
    }

    public void setRqty(String rqty) {
        this.rqty = rqty;
    }

    public String getCart() {
        return cart;
    }

    public void setCart(String cart) {
        this.cart = cart;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getStock_update() {
        return stock_update;
    }

    public void setStock_update(String stock_update) {
        this.stock_update = stock_update;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpressStock() {
        return expressStock;
    }

    public void setExpressStock(String expressStock) {
        this.expressStock = expressStock;
    }

    public String getWish() {
        return wish;
    }

    public void setWish(String wish) {
        this.wish = wish;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getIdeal() {
        return ideal;
    }

    public void setIdeal(String ideal) {
        this.ideal = ideal;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getFit() {
        return fit;
    }

    public void setFit(String fit) {
        this.fit = fit;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getClosure() {
        return closure;
    }

    public void setClosure(String closure) {
        this.closure = closure;
    }

    public String getPocket() {
        return pocket;
    }

    public void setPocket(String pocket) {
        this.pocket = pocket;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getRise() {
        return rise;
    }

    public void setRise(String rise) {
        this.rise = rise;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getBestselling() {
        return bestselling;
    }

    public void setBestselling(String bestselling) {
        this.bestselling = bestselling;
    }

    public String getPricedrop() {
        return pricedrop;
    }

    public void setPricedrop(String pricedrop) {
        this.pricedrop = pricedrop;
    }


    public static String getColumnVariation() {
        return COLUMN_VARIATION;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public static String getColumnVariationid() {
        return COLUMN_VARIATIONID;
    }

    public String getVariationId() {
        return variationId;
    }

    public void setVariationId(String variationId) {
        this.variationId = variationId;
    }
}