package lol.clann.minecraft.plugin.taobao.mapper;

import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lol.clann.minecraft.plugin.taobao.model.domain.DealLog;
import lol.clann.minecraft.plugin.taobao.model.domain.Shop;
import lol.clann.minecraft.plugin.taobao.model.domain.ShopItem;
import lol.clann.minecraft.plugin.taobao.model.domain.ShopStatistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author pyz
 * @date 2019/6/5 7:43 PM
 */
public interface TaobaoMapper {

    List<Shop> fetchShopMenuContent(@Param("offset") int offset, @Param("count") int count);

    int fetchShopCount();

    List<ShopItem> fetchShopContent(@Param("owner") String owner, @Param("type") String type, @Param("offset") int offset, @Param("count") int count);

    int fetchShopItemCount(@Param("owner") String owner, @Param("type") String type);

    ShopItem fetchShopItemById(@Param("shopItemId") long shopItemId);

    ShopItem fetchShopItemByIdForUpdate(@Param("shopItemId") long shopItemId);

    boolean isSysShop(@Param("shopId") long shopId);

    long calcSysShopDailyCost(@Param("player") String player);

    void updateShopItemAfterDeal(@Param("shopItemId") long shopItemId, @Param("count") long count);

    void insertDealLog(DealLog log);

    ShopItem fetchShopItemByOrder(@Param("shopId") long shopId, @Param("type") ShopTypeEnum type, @Param("order") int order);

    void update(ShopItem shopItem);

    Long fetchShopIdByOwner(@Param("owner") String owner);

    Shop fetchShopByIdForUpdate(@Param("id") Long id);

    int fetchMaxOrder(@Param("id") long id, @Param("type") String type);

    void insertShopItem(ShopItem shopItem);

    void deleteShopItem(@Param("id") long id);

    void deleteShop(@Param("shopId") long shopId);

    void deleteShopItemByShopId(@Param("shopId") long shopId);

    void clearShopItemCount(@Param("shopId") long shopId, @Param("type") ShopTypeEnum type);

    void updateShop(Shop shop);

    void insertShop(Shop shop);

    void setAllShopItemCountAndCapabilityByShopId(long shopId);

    void updateShopStatistics(@Param("shopId")long shopId);
}
