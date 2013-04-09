package org.springframework.richclient.samples.dataeditor.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SupplierService
{
    private static final Map<Integer, Supplier> REPOSITORY = new HashMap<Integer, Supplier>();

    static
    {
        REPOSITORY.put(1, new Supplier("Jake Johnson","jake@springcource.com","555-5236","SpringCource","555-3636"));
        REPOSITORY.put(2, new Supplier("Tim Sears","tim@microshoft.com","555-3634","Microshoft","555-8978"));
        REPOSITORY.put(3, new Supplier("Peter Deloye","peter@ibmn.com","525-6636","IBMN","556-5654"));
    }

    @SuppressWarnings("unchecked")
    public List<Supplier> findSuppliers(final SupplierFilter filter)
    {
        List<Supplier> filtered = new ArrayList<Supplier>();
        for (Supplier supplier : REPOSITORY.values())
        {
            if (checkFilter(supplier, filter))
            {
                filtered.add(supplier);
            }
        }
        return filtered;
    }

    public Supplier getSupplier(Integer id)
    {
        return REPOSITORY.get(id);
    }

    public boolean checkFilter(Supplier supplier, SupplierFilter filter)
    {
        boolean nameOk = true;
        boolean contactNameOk = true;
        if (filter.getNameContains() != null)
            nameOk = supplier.getName().contains(filter.getNameContains());
        if (filter.getContactNameContains() != null)
            contactNameOk = supplier.getContactName().contains(filter.getContactNameContains());
        return nameOk && contactNameOk;
    }
}
