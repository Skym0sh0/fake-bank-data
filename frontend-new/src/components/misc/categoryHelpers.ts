import {Category} from "@api/api.ts";
import * as _ from "lodash";

export type LookupById<T> = { [id: string]: T };
export type LookupByName<T> = { [name: string]: T };

export type CategoriesById = LookupById<Category>;

export function mapCategoriesById(categories?: Category[]): CategoriesById {
  return (categories ?? []).reduce((old, cur) => {
    if (!cur.id)
      return old;
    return ({...old, [cur.id]: cur});
  }, {})
}

export type CategoriesByName = LookupByName<Category>;

export function mapCategoriesByName(categories: Category[]): CategoriesByName {
  return (categories ?? []).reduce((old, cur) => ({...old, [cur.name]: cur}), {})
}

export type CategoryWithParentNamesChain = Category & { parentCategoryNames: string[] };
export type CategoryWithParentNamesChained<T> = CategoryWithParentNamesChain & { parentChain?: T };

export function flatCategoryTreeWithParentChain<T>(rootCategories?: Category[], parentChainTransformer?: (names: string[]) => T): CategoryWithParentNamesChained<T>[] {
  const extract = (cat: Category, parentCategoryNames: string []): CategoryWithParentNamesChain[] => {
    return [
      {
        ...cat,
        parentCategoryNames: parentCategoryNames
      },
      ...(cat.subCategories ?? []).flatMap(c => extract(c, [...parentCategoryNames, cat.name]))
    ];
  };

  const flatted: CategoryWithParentNamesChained<T>[] = (rootCategories || []).flatMap(c => extract(c, []))
    .map(c => ({
        ...c,
        parentChain: parentChainTransformer?.([...c.parentCategoryNames, c.name])
      })
    )

  return _.sortBy(flatted, cat => cat.name.toLowerCase());
}
