package org.damage.db;

import org.damage.business.DamagedHouseTO;
import org.damage.business.DamagedHouseMoreInfoTO;
import org.damage.business.HouseFacilityInfoTO;
import org.erms.db.*;
import org.erms.db.DBConstants;
import org.erms.db.SQLGenerator;
import org.erms.business.KeyValueDTO;
import org.sahana.share.db.AbstractDataAccessManager;
import org.sahana.share.utils.OrderedMap;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.*;


public class DataAccessManager extends AbstractDataAccessManager implements DBConstants {

    public DataAccessManager() throws Exception {
    }

    public boolean addDamagedHouse(DamagedHouseTO dhTO) throws SQLException, Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean status = false;

        try {
            connection = DBConnection.createConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(org.damage.db.SQLGenerator.getSQLForAddDamagedHouse());

            preparedStatement.setString(1, dhTO.getDistrictCode());
            preparedStatement.setString(2, dhTO.getDivision());
            preparedStatement.setString(3, dhTO.getGSN());
            preparedStatement.setString(4, dhTO.getOwner());
            preparedStatement.setDouble(5, dhTO.getDistanceFromSea());
            preparedStatement.setString(6, dhTO.getCity());
            preparedStatement.setString(7, dhTO.getNoAndStreet());
            preparedStatement.setString(8, dhTO.getCurrentAddress());
            preparedStatement.setDouble(9, dhTO.getFloorArea());
            preparedStatement.setInt(10, dhTO.getNoOfStories());
            preparedStatement.setString(11, dhTO.getTypeOfOwnership());
            preparedStatement.setInt(12, dhTO.getNoOfResidents());
            preparedStatement.setString(13, dhTO.getTypeOfConstruction());
            preparedStatement.setString(14, dhTO.getPropertyTaxNo());
            preparedStatement.setDouble(15, dhTO.getTotalDamagedCost());
            preparedStatement.setDouble(16, dhTO.getLandArea());
            preparedStatement.setBoolean(17, dhTO.getRelocate());
            preparedStatement.setBoolean(18, dhTO.getInsured());
            preparedStatement.setString(19, dhTO.getDamageType());
            preparedStatement.setString(20, dhTO.getDamageType());
            preparedStatement.executeUpdate();


            Iterator ite1 = dhTO.getDamagedHouseMoreInfoList().iterator();
            preparedStatement = connection.prepareStatement(org.damage.db.SQLGenerator.getSQLForAddDamagedHouse_Damage_Moreinfo());
            if (ite1.hasNext()) {
                DamagedHouseMoreInfoTO dhmInfoTO = (DamagedHouseMoreInfoTO) ite1.next();
                preparedStatement.setInt(1, dhmInfoTO.getHouseID());
                preparedStatement.setString(2, dhmInfoTO.getDamageInfo());
                preparedStatement.executeUpdate();
            }

            Iterator ite2 = dhTO.getHouseFacilityInfoList().iterator();
            preparedStatement = connection.prepareStatement(org.damage.db.SQLGenerator.getSQLForAddDamagedHouse_Damage_Moreinfo());
            if (ite2.hasNext()) {
                HouseFacilityInfoTO hfInfoTO = (HouseFacilityInfoTO) ite2.next();
                preparedStatement.setInt(1, hfInfoTO.getHouseID());
                preparedStatement.setString(2, hfInfoTO.getFacilityName());
                preparedStatement.setString(3, hfInfoTO.getDescription());
                preparedStatement.executeUpdate();
            }

        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    status = false;

                }

            } catch (SQLException e1) {
                e1.printStackTrace();
                status = false;
                throw e1;

            }
            status = false;
            e.printStackTrace();

        } finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
        return status;
    }
    //list oif house dtos
    //public List searchHouses(SearchHouseTO);


}
