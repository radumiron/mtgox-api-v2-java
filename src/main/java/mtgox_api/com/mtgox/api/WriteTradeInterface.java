package mtgox_api.com.mtgox.api;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/24/13
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface WriteTradeInterface {
    /**
     * Withdraws an amount of BTC from the user's mtg account to a BTC address.
     * @param  amount  the amount of BTC to withdraw (min 0.1)
     * @param  dest_address  the address of the BTC wallet
     * @return      the transaction id (if success), (???) if failed //TODO
     */
    public String withdrawBTC(double amount, String dest_address);

    /**
     * Sells BTC at market price
     * @param  amount  the amount of BTC to sell (min 0.1)
     * @return  the transaction id (if success), (???) if failed //TODO
     */
    public String sellBTC(double amount);

    /**
     * Sells BTC at market price
     * @param  amount  the amount of BTC to buy (min 0.1)
     * @return  the transaction id (if success), (???) if failed //TODO
     */
    public String buyBTC(double amount);
}
