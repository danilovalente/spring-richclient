package org.springframework.richclient.samples.dataeditor.ui;

import org.springframework.richclient.samples.dataeditor.domain.Supplier;
import org.springframework.richclient.samples.dataeditor.domain.SupplierFilter;
import org.springframework.richclient.samples.dataeditor.domain.SupplierService;
import org.springframework.richclient.widget.editor.provider.AbstractDataProvider;

import java.util.List;

public class SupplierDataProvider extends AbstractDataProvider
{
    private SupplierService service;

    public SupplierDataProvider(SupplierService service)
    {
        this.service = service;
    }

    public boolean supportsFiltering()
    {
        return true;
    }

    public List getList(Object criteria)
    {
        if (criteria instanceof SupplierFilter)
        {
            return service.findSuppliers((SupplierFilter) criteria);
        }
        else if (criteria instanceof Supplier)
        {
            return service.findSuppliers(SupplierFilter.fromSupplier((Supplier) criteria));
        }
        else
        {
            throw new IllegalArgumentException("This provider can only filter through SupplierFilter, not " + criteria.getClass());
        }
    }

    public boolean supportsUpdate()
    {
        return true;
    }

    @Override
    public Object doCreate(Object newData)
    {
        return newData;
    }

    @Override
    public void doDelete(Object dataToRemove)
    {
    }

    @Override
    public Object doUpdate(Object updatedData)
    {
        return updatedData;
    }

    public boolean supportsCreate()
    {
        return true;
    }

    public boolean supportsClone()
    {
        return false;
    }

    public boolean supportsDelete()
    {
        return true;
    }
}
