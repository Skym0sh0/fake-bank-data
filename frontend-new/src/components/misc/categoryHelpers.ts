import {Category} from "@api/api.ts";
import * as _ from "lodash";

export function mapCategoriesById(categories?: Category[]): { [id: string]: Category } {
    return (categories ?? []).reduce((old, cur) => ({...old, [cur.id]: cur}), {})
}

export function mapCategoriesByName(categories: Category[]): Category[] {
    return (categories ?? []).reduce((old, cur) => ({...old, [cur.name]: cur}), {})
}

export type CategoryWithParentNamesChain = Category & { parentCategoryNames: string };

export function flatCategoryTreeWithParentChain(rootCategories?: Category[], parentChainTransformer?: (names: string[]) => string): CategoryWithParentNamesChain[] {
    const extract = (cat: Category, parentCategoryNames: string []): CategoryWithParentNamesChain => {
        return [
            {
                ...cat,
                parentCategoryNames: parentCategoryNames
            },
            ...cat.subCategories.flatMap(c => extract(c, [...parentCategoryNames, cat.name]))
        ];
    };

    const flatted = (rootCategories || []).flatMap(c => extract(c, []))
        .map(c => ({
                ...c,
                parentChain: parentChainTransformer?.([...c.parentCategoryNames, c.name])
            })
        )

    return _.sortBy(flatted, cat => cat.name.toLowerCase());
}
