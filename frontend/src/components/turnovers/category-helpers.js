import _ from 'lodash'

export function flatCategoryTreeWithParentChain(rootCategories, parentChainTransformer) {
    const extract = (cat, parentCategoryNames) => {
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
                parentChain: parentChainTransformer ? parentChainTransformer([...c.parentCategoryNames, c.name]) : undefined
            })
        )

    return _.sortBy(flatted, cat => cat.name.toLowerCase());
}

export function mapCategoriesByName(flatCategories) {
    return (flatCategories || []).reduce((old, cur) => ({...old, [cur.name]: cur}), {})
}

export function mapCategoriesById(flatCategories) {
    return (flatCategories || []).reduce((old, cur) => ({...old, [cur.id]: cur}), {})
}
