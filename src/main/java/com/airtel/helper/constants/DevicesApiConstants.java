package com.airtel.helper.constants;

public interface DevicesApiConstants {

	static final String DEVICES_PRICE_PATH = "/airtel-devices-platform/rest/devices/meta/v1/price/fetch";
	static final String DEVICES_INVENTORY_PATH = "/airtel-devices-platform/rest/devices/meta/v1/inventory/fetch";
	static final String DEVICES_CONTENT_PATH = "/airtel-devices-platform/rest/devices/v2/getProducts";
	static final String DEVICES_AUTOSUGGEST_PATH = "/airtel-devices-platform/rest/devices/v2/getSearchResults";
	final static String CONFIGURATION_FILE_PATH = "config/envConfig.properties";
	final static String DB_FILE_NAME = "ProductData.txt";
	//final static String PRODUCT_DATA_QUERY = "SELECT blcs.name AS PRODUCTNAME,10000055 AS CATEGORYID,TEMP_TABLE.* FROM (SELECT prod.product_id as PRODUCTID,  prod.model_no as MODELNAME,  CONCAT(prod.internal_memory,'GB') AS STORAGE,  sku.sku_id AS SKUID,  opt.attribute_value AS COLOR,  apov.color_code AS COLORCODE,  bm.url AS IMAGEURL,  sku.retail_price AS RETAILPRICE,  sku.sale_price AS SALEPRICE,  plan.emi_per_month AS EMI,  PLAN.TENURE AS TENURE,  CASE WHEN inv.quantity_available > 10 THEN 'true' ELSE 'false' END AS INVENTORY, CASE WHEN prod.IS_PLANPRODUCT = 1 THEN 'true' ELSE 'false' END AS ISPLANPRODUCT, Rtrim(Xmlagg(XMLELEMENT(e, bpa.name||':'||bpa.value, 'devicePlatform').EXTRACT('//text()')).GetClobVal(), ',') FEATURES,brand.name as BRANDNAME FROM blc_sku_option_value_xref sxref,  blc_product_option_value opt,  airtel_product_option_value apov,  blc_sku sku,  airtel_product prod,  airtel_plan PLAN,  blc_sku_media_map MAP,  blc_media bm,  blc_inventory inv,  blc_product_attribute bpa,  blc_category brand,  blc_category_product_xref catProd WHERE bpa.product_id = prod.PRODUCT_ID  AND catProd.PRODUCT_ID = prod.PRODUCT_ID  AND catProd.category_id = brand.category_id  AND sxref.product_option_value_id = opt.product_option_value_id  AND sku.sku_id = sxref.sku_id  AND sku.sku_id = inv.sku_id  AND prod.product_id = sku.addl_product_id  AND apov.product_option_value_id = opt.product_option_value_id  AND PLAN.id = prod.plan_id  AND sxref.archived = 'N'  AND sku.archived = 'N'  AND map.archived = 'N'  AND bm.archived = 'N'  AND map_key = 'primary'  AND map.blc_sku_sku_id = sku.sku_id  AND sku.addl_product_id IS NOT NULL  AND map.media_id = bm.media_id GROUP BY prod.model_no,  prod.product_id,  prod.internal_memory,  brand.category_id,  brand.name,  sku.sku_id,  opt.attribute_value,  bm.url,  sku.retail_price,  sku.sale_price,  apov.color_code,  sku.name,  plan.emi_per_month,  PLAN.TENURE,  inv.quantity_available, prod.IS_PLANPRODUCT) TEMP_TABLE JOIN blc_sku blcs ON PRODUCTID=blcs.default_product_id ORDER BY RETAILPRICE asc";
	final static String PRODUCT_DATA_QUERY = "SELECT blcs.name AS PRODUCTNAME,TEMP_TABLE.* FROM (SELECT prod.product_id as PRODUCTID,  prod.model_no as MODELNAME,  CONCAT(prod.internal_memory,'GB') AS STORAGE,  sku.sku_id AS SKUID,  opt.attribute_value AS COLOR,  apov.color_code AS COLORCODE,  bm.url AS IMAGEURL,  sku.retail_price AS RETAILPRICE,  sku.sale_price AS SALEPRICE,  plan.emi_per_month AS EMI,  PLAN.TENURE AS TENURE,  CASE WHEN inv.quantity_available > 10 THEN 'true' ELSE 'false' END AS INVENTORY, CASE WHEN prod.IS_PLANPRODUCT = 1 THEN 'true' ELSE 'false' END AS ISPLANPRODUCT, brand.name as BRANDNAME,brand.category_id AS CATEGORYID FROM blc_sku_option_value_xref sxref,  blc_product_option_value opt,  airtel_product_option_value apov,  blc_sku sku,  airtel_product prod,  airtel_plan PLAN,  blc_sku_media_map MAP,  blc_media bm,  blc_inventory inv,  blc_product_attribute bpa,  blc_category brand,  blc_category_product_xref catProd WHERE bpa.product_id = prod.PRODUCT_ID  AND catProd.PRODUCT_ID = prod.PRODUCT_ID  AND catProd.category_id = brand.category_id  AND sxref.product_option_value_id = opt.product_option_value_id  AND sku.sku_id = sxref.sku_id  AND sku.sku_id = inv.sku_id  AND prod.product_id = sku.addl_product_id  AND apov.product_option_value_id = opt.product_option_value_id  AND PLAN.id = prod.plan_id  AND sxref.archived = 'N'  AND sku.archived = 'N'  AND map.archived = 'N'  AND bm.archived = 'N'  AND map_key = 'primary'  AND map.blc_sku_sku_id = sku.sku_id  AND sku.addl_product_id IS NOT NULL  AND map.media_id = bm.media_id GROUP BY prod.model_no,  prod.product_id,  prod.internal_memory,  brand.category_id,  brand.name,  sku.sku_id,  opt.attribute_value,  bm.url,  sku.retail_price,  sku.sale_price,  apov.color_code,  sku.name,  plan.emi_per_month,  PLAN.TENURE,  inv.quantity_available, prod.IS_PLANPRODUCT,brand.category_id) TEMP_TABLE JOIN blc_sku blcs ON PRODUCTID=blcs.default_product_id ORDER BY RETAILPRICE asc";
	final static String DEVICES_TESTDATA_PATH = "src/test/resources/test_data/Devices/ContentTestData/";
	final static String DEVICES_CONTENT_FILENAME1 = "contentTestData1.cfg";
	final static String DEVICES_CONTENT_FILENAME2 = "contentTestData2.cfg";
	final static String DEVICES_CONTENT_FILENAME3 = "contentTestData3.cfg";
	final static String DEVICES_CONTENT_FILENAME = "contentTestData.cfg";
	final static String COLUMN_SEPERATOR = "---";
	final static String FEATURE_SEPERATOR = ":::";
	final static int PRODUCT_NAME_POSITION = 0;
	final static int PRODUCT_ID_POSTION = 1;
	final static int MODEL_NO_POSTION = 2;
	final static int STORAGE_POSTION = 3;
	final static int SKU_ID_POSTION = 4;
	final static int COLOR_POSTION = 5;
	final static int COLOR_CODE_POSTION = 6;
	final static int MEDIA_URL_POSTION = 7;
	final static int RETAIL_PRICE_POSTION = 8;
	final static int DOWN_PAYMENT_POSTION = 9;
	final static int EMI_POSTION = 10;
	final static int TENURE_POSITION = 11;
	final static int INVENTORY_POSITION = 12;
	final static int PLANPRODUCT_POSITION = 13;
	//final static int FEATURES_POSITION = 15;
	final static int BRAND_NAME_POSTION = 14;
	final static int CATEGORY_ID_POSTION = 15;
	final static String UNIX_COMMAND_DELIMITER = " | ";
	final static String COMPARATOR_IGNORE_FILE_PATH = "config/ignoreFromComparison.properties";
	final static String OBJECT_CONVERSION_FAILURE = "conversionFailure";
	final static String UNIX_IF_CONDITION = "' '{if($";
	final static String UNIX_AWK_COMMAND = "awk -F '";
	final static String UNIX_AWK_PRINT_COMMAND = ") print $0}'";
}
