import {openKlantPluginSpecification} from './open-klant.plugin.specification';

describe('openKlantPluginSpecification', () => {
    const functionKeys = Object.keys(openKlantPluginSpecification.functionConfigurationComponents ?? {});
    const languages = Object.keys(openKlantPluginSpecification.pluginTranslations ?? {});

    it('registers the plugin under the openklant id', () => {
        expect(openKlantPluginSpecification.pluginId).toBe('openklant');
    });

    it('registers a configuration component for every plugin action', () => {
        expect(functionKeys.length).toBeGreaterThan(0);
        functionKeys.forEach(key => {
            expect(openKlantPluginSpecification.functionConfigurationComponents![key])
                .withContext(`missing component for '${key}'`)
                .toBeDefined();
        });
    });

    it('translates every plugin action in every supported language', () => {
        expect(languages).toEqual(['nl', 'en']);

        languages.forEach(language => {
            const translations = openKlantPluginSpecification.pluginTranslations![language];
            functionKeys.forEach(key => {
                expect(translations[key])
                    .withContext(`missing ${language} translation for action '${key}'`)
                    .toBeTruthy();
            });
        });
    });

    it('defines the same translation keys in every supported language', () => {
        const [nl, en] = languages.map(language =>
            Object.keys(openKlantPluginSpecification.pluginTranslations![language]).sort()
        );

        expect(nl).toEqual(en);
    });

    it('has no duplicate action keys', () => {
        expect(new Set(functionKeys).size).toBe(functionKeys.length);
    });
});
