package com.storeapp.ui;

import com.storeapp.model.*;
import com.storeapp.service.Logger;
import com.storeapp.service.RandomDataGenerator;
import com.storeapp.service.Store;
import com.storeapp.ui.navigation.Navigation;
import com.storeapp.util.InputValidator;
import com.storeapp.util.Constants;
import java.io.IOException;
import java.util.List;

public class ProductManager {

	private final Store store;
	private final InputValidator validator;
	private final Navigation navigation;
    
	private static final String[] PRODUCT_MENU_OPTIONS = {
		    "1. Show all products",
		    "2. View product details",
		    "3. Add product",
		    "4. Edit product",
		    "5. Delete product",
		    "6. Search product",
		    "7. Generate Sample Products",
		    "8. Back to admin menu"
		};
    
    public ProductManager(Store store,
            InputValidator validator,
            Navigation navigation) {
		this.store = store;
		this.validator = validator;
		this.navigation = navigation;
		}
    
    public void showMenu() {

        navigation.push("Products");

        try {

            while (true) {

                navigation.printBreadcrumb();

                validator.printBox(
                    "PRODUCT MANAGEMENT",
                    PRODUCT_MENU_OPTIONS
                );

                int choice = validator.readIntRange(
                    1,
                    PRODUCT_MENU_OPTIONS.length
                );

                if (!handleChoice(choice)) {
                    return;
                }
            }

        } finally {
            navigation.pop();
        }
    }
    
    private boolean handleChoice(int choice) {

        switch (choice) {

            case 1:
                showAllProducts();
                break;

            case 2:
                viewProductDetails();
                break;

            case 3:
                addProduct();
                break;

            case 4:
                editProduct();
                break;

            case 5:
                deleteProduct();
                break;

            case 6:
                searchProduct();
                break;

            case 7:
                generateSampleProducts();
                break;

            case 8:
                return false;
        }

        return true;
    }
    
    
    private void showAllProducts() {

        navigation.push("Show Products");

        try {

            navigation.printBreadcrumb();

            List<Product> products = store.getProducts();

            if (products.isEmpty()) {
                System.out.println("\nNo products in the store.");
                return;
            }

            printProductTable(products);

            validator.pause();

        } finally {
            navigation.pop();
        }
    }
    
    private void printProductTable(List<Product> products) {

        System.out.println("\n--- Product List ---");

        printProductHeader();

        int index = 1;

        for(Product product : products){

            printProductRow(product, index);

            index++;
        }

        printProductFooter();
    }
    
    private void printProductHeader(){

        System.out.printf(
            "%-4s %-15s %-10s %12s %8s %-8s%n",
            "#",
            "Name",
            "Code",
            "Price",
            "Stock",
            "Unit"
        );

        System.out.println(
            "---- --------------- ---------- ------------ -------- --------"
        );
    }
    
    private void printProductRow(Product product, int index){

        System.out.printf(
            "%-4d %-15s %-10s %,12d %8.1f %-8s%n",
            index,
            product.getName(),
            product.getCode(),
            (long) product.getPrice(),
            product.getStock(),
            product.getUnitType()
        );
    }
    
    private void printProductFooter(){

        System.out.println(
            "---- --------------- ---------- ------------ -------- --------"
        );
    }
    


    private void addProduct(){

        navigation.push("Add Product");

        try {

            Product product = createProduct();

            store.addProduct(product);
            store.save();

            logProductCreated(product);

            System.out.println(
                "✅ Product added successfully!"
            );

        } finally {
            navigation.pop();
        }
    }
    
    private Product createProduct() {

        System.out.println("\n--- Add New Product ---");

        String code = readUniqueProductCode();

        String name = validator.readNonEmptyString(
                "Name: "
        );

        System.out.print("Price: ");
        double price = validator.readPositiveDouble();

        System.out.print("Stock: ");
        double stock = validator.readPositiveDouble();

        UnitType unitType = validator.readUnitType();


        Product.Builder builder =
                new Product.Builder(
                        code,
                        name,
                        price,
                        stock,
                        unitType
                );


        if (validator.yesOrNo(
                "Do you want to add optional details?"
        )) {

            addOptionalDetails(builder);
        }


        return builder.build();
    }
    
    private String readUniqueProductCode() {

        while(true) {

            String code =
                    validator.readNonEmptyString(
                        "Code: "
                    );

            if(store.findItemByCode(code) == null) {
                return code;
            }

            System.out.println(
                "❌ This code already exists. Please enter a different code."
            );
        }
    }
    
    private void addOptionalDetails(Product.Builder builder) {

        System.out.println("\n--- Optional Fields ---");
        System.out.println("Press enter to skip");


        String manufacturer =
                validator.readOptionalString(
                        "Manufacturer: "
                );

        if(manufacturer != null) {
            builder.manufacturer(manufacturer);
        }


        String color =
                validator.readOptionalString(
                        "Color: "
                );

        if(color != null) {
            builder.color(color);
        }


        System.out.print("Weight (kg, 0 to skip): ");
        double weight = validator.readPositiveDouble();

        if(weight > 0) {
            builder.weight(weight);
        }


        System.out.print("Volume (L, 0 to skip): ");
        double volume = validator.readPositiveDouble();

        if(volume > 0) {
            builder.volume(volume);
        }


        String description =
                validator.readOptionalString(
                        "Description: "
                );

        if(description != null) {
            builder.description(description);
        }


        System.out.print("Discount Percent (0-100): ");

        double discount =
                validator.readDiscountPercent();

        builder.discountPercent(discount);
    }
    
    private void logProductCreated(Product product) {

        Logger.info(
            "PRODUCT_CREATED | Code="
            + product.getCode()
            + " | Name="
            + product.getName()
            + " | Price="
            + product.getPrice()
            + " | Stock="
            + product.getStock()
            + " | Unit="
            + product.getUnitType()
        );
    }

    private void searchProduct() {

        navigation.push("Search Product");

        try {

            navigation.printBreadcrumb();
            System.out.println("\n--- Search Product ---");
            String keyword = validator.readNonEmptyString(
                    "Enter keyword to search (name or code): "
            );
            List<Product> results = store.searchItems(keyword);
            if (results.isEmpty()) {

                System.out.println(
                    "No products found matching '" 
                    + keyword 
                    + "'."
                );

                return;
            }


            System.out.println(
                "\n--- Search Results for '" 
                + keyword 
                + "' ---"
            );


            printProductTable(results);


            validator.pause();


        } finally {

            navigation.pop();
        }
    }
    
    
    private void deleteProduct() {

        navigation.push("Delete Product");

        try {

            navigation.printBreadcrumb();

            System.out.println("\n--- Delete Product ---");

            String code = validator.readNonEmptyString(
                    "Enter the product code to delete: "
            );


            Product product = store.findItemByCode(code);


            if (product == null) {

                Logger.warning(
                    "PRODUCT_DELETE_FAILED | Code="
                    + code
                    + " | Reason=NOT_FOUND"
                );

                System.out.println("❌ No product found");

                return;
            }


            if (!validator.yesOrNo(
                    "Are you sure you want to delete '"
                    + product.getName()
                    + "'?"
            )) {

                System.out.println("❌ Deletion cancelled.");

                return;
            }


            store.removeProduct(product);

            store.save();


            System.out.println(
                "✅ Product '"
                + product.getName()
                + "' deleted successfully!"
            );


            logProductDeleted(product);


        } finally {

            navigation.pop();
        }
    }
    
    private void logProductDeleted(Product product){

        Logger.warning(
            "PRODUCT_DELETED | Code="
            + product.getCode()
            + " | Name="
            + product.getName()
            + " | PreviousStock="
            + product.getStock()
            + " | PreviousPrice="
            + product.getPrice()
        );
    }
    
    
    private void editProduct() {

        navigation.push("Edit Product");

        try {

            navigation.printBreadcrumb();

            Product product = findProductForEdit();

            if (product == null) {
                return;
            }

            double oldPrice = product.getPrice();
            double oldStock = product.getStock();
            double oldDiscount = product.getDiscountPercent();


            updateBasicFields(product);

            updateOptionalFields(product);


            store.save();

            logProductUpdated(
                    product,
                    oldPrice,
                    oldStock,
                    oldDiscount
            );


            System.out.println(
                "✅ Product '" 
                + product.getName() 
                + "' updated successfully!"
            );


        } finally {

            navigation.pop();
        }
    }
    
    
    private Product findProductForEdit() {

        System.out.println("\n--- Edit Product ---");

        String code =
                validator.readNonEmptyString(
                        "Enter product code: "
                );


        Product product =
                store.findItemByCode(code);


        if(product == null){

            System.out.println(
                "❌ No product found with code '" 
                + code 
                + "'."
            );

        }


        return product;
    }
    
    
    private void logProductUpdated(
            Product product,
            double oldPrice,
            double oldStock,
            double oldDiscount
    ) {

        Logger.info(
            "PRODUCT_UPDATED | Code="
            + product.getCode()
            + " | OldPrice=" + oldPrice
            + " | NewPrice=" + product.getPrice()
            + " | OldStock=" + oldStock
            + " | NewStock=" + product.getStock()
            + " | OldDiscount=" + oldDiscount
            + " | NewDiscount=" + product.getDiscountPercent()
        );
    }
    
    private void updateBasicFields(Product product){

        System.out.println(
            "\nEditing: " 
            + product.getName()
        );

        System.out.println(
            "(Press Enter to keep current value)\n"
        );


        System.out.print(
        	    "New price ("
        	    + String.format("%,.0f", product.getPrice())
        	    + " Tomans): "
        	);

        Double price =
                validator.readOptionalDouble();


        if(price != null && price > 0){
            product.setPrice(price);
        }



        System.out.print(
            "New stock (" 
            + product.getStock()
            + "): "
        );

        Double stock =
                validator.readOptionalDouble();


        if(stock != null && stock >= 0){
            product.setStock(stock);
        }



        System.out.print(
            "New discount (" 
            + product.getDiscountPercent()
            + "%): "
        );


        Double discount =
                validator.readOptionalDouble();


        if(discount != null &&
           discount >= 0 &&
           discount <=100){

            product.setDiscountPercent(discount);
        }

    }
    
    private void updateOptionalFields(Product product){

        updateManufacturer(product);

        updateColor(product);

        updateWeight(product);

        updateVolume(product);

        updateDescription(product);

    }
    
    private void updateManufacturer(Product product){

        String current =
                product.getManufacturer();


        if(current == null){

            if(validator.yesOrNo(
                "Add manufacturer?"
            )){

                product.setManufacturer(
                    validator.readNonEmptyString(
                        "Manufacturer: "
                    )
                );
            }

        } else {


            if(validator.yesOrNo(
            		"Edit manufacturer? (Current: "
            				+ current
            				+ ")"
            )){

                product.setManufacturer(
                    validator.readNonEmptyString(
                        "New manufacturer: "
                    )
                );
            }
        }
    }
    
    
    private void updateColor(Product product) {

        String current = product.getColor();

        if (current == null) {

            if (validator.yesOrNo("Add color?")) {

                product.setColor(
                    validator.readNonEmptyString(
                        "New Color: "
                    )
                );
            }

        } else {

            if (validator.yesOrNo(
            		"Edit color? (Current: " + current + ")"
            )) {

                product.setColor(
                    validator.readNonEmptyString(
                        "New color: "
                    )
                );
            }
        }
    }
    
    
    private void updateWeight(Product product) {

        Double current = product.getWeight();

        if (current == null) {

        	if (validator.yesOrNo("Add weight?")) {

        	    System.out.print("Weight (kg): ");

        	    double weight =
        	            validator.readPositiveDouble();

        	    product.setWeight(weight);
        	}

        } else {
        	System.out.println(
        		    "Current weight: "
        		    + current
        		    + " kg"
        		);

        		if (validator.yesOrNo("Edit weight?")) {

        		    System.out.print("New weight (kg): ");

        		    double weight =
        		            validator.readPositiveDouble();

        		    product.setWeight(weight);
        		}
        }
    }
    
    private void updateVolume(Product product) {

        Double current = product.getVolume();

        if (current == null) {

        	if (validator.yesOrNo("Add volume?")) {

        	    System.out.print("Volume (L): ");

        	    double volume =
        	            validator.readPositiveDouble();

        	    product.setVolume(volume);
        	}

        } else {

        	System.out.println(
        		    "Current volume: "
        		    + current
        		    + " L"
        		);

        		if (validator.yesOrNo("Edit volume?")) {

        		    System.out.print("New volume (L): ");

        		    double volume =
        		            validator.readPositiveDouble();

        		    product.setVolume(volume);
        		}
        }
    }
    
    private void updateDescription(Product product) {

        String current = product.getDescription();


        if (current == null) {

            if (validator.yesOrNo("Add description?")) {

                product.setDescription(
                    validator.readNonEmptyString(
                        "Description: "
                    )
                );
            }

        } else {


            if (validator.yesOrNo(
                    "Edit description (current: "
                    + current
                    + ")?"
            )) {

                product.setDescription(
                    validator.readNonEmptyString(
                        "New description: "
                    )
                );
            }
        }
    }
    
    private void viewProductDetails() {
    	navigation.push("Product Details");
    	navigation.printBreadcrumb();
        System.out.println("\n--- View Product Details ---");
        String code = validator.readNonEmptyString("Product code: ");
        Product p = store.findItemByCode(code);
        if (p == null) {
            System.out.println("❌ No product found.");
            navigation.pop();
            validator.pause();
            return;
        }

        System.out.println("Code        : " + p.getCode());
        System.out.println("Name        : " + p.getName());
        System.out.println("Price       : " + String.format("%,.0f Tomans", p.getPrice()));
        System.out.println("Stock       : " + p.getStock());
        System.out.println("Unit        : " + p.getUnitType());
        System.out.println("Discounted  : " + String.format("%,.0f Tomans", p.getDiscountedPrice()));
        if (p.getManufacturer() != null)
            System.out.println("Made by     : " + p.getManufacturer());
        if (p.getColor() != null)
            System.out.println("Color       : " + p.getColor());
        if (p.getWeight() != null)
            System.out.println("Weight      : " + p.getWeight() + " kg");
        if (p.getVolume() != null)
            System.out.println("Volume      : " + p.getVolume() + " L");
        if (p.getDescription() != null)
            System.out.println("Description : " + p.getDescription());
        if (p.getDiscountPercent() > 0)
            System.out.println("Discount    : " + p.getDiscountPercent() + "%");
        if (p.getProductionDate() != null)
            System.out.println("Prod. Date  : " + p.getProductionDate());
        if (p.getExpirationDate() != null)
            System.out.println("Exp. Date   : " + p.getExpirationDate());
        System.out.println("────────────────────────────────────");
        Logger.debug(
        	    "PRODUCT_VIEW | Code="
        	    + code
        	);
        
        navigation.pop();
        validator.pause();
    }
    
    
    private void generateSampleProducts() {
    	navigation.push("Generate Samples");
    	navigation.printBreadcrumb();
        System.out.print("How many random products? (1-10)\n");
        int count = validator.readIntRange(1, 10);
        RandomDataGenerator gen = new RandomDataGenerator(store);
        gen.generateProducts(count);
        store.save();
        Logger.info(
        	    "SAMPLE_PRODUCTS_GENERATED | Count="
        	    + count
        	);
        System.out.println("✅ " + count + " sample products generated and saved!");
        navigation.pop();
    } 
    
}