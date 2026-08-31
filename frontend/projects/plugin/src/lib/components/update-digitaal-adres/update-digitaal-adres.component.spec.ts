import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UpdateDigitaalAdresComponent} from './update-digitaal-adres.component';
import {DigitaalAdresConfig} from '../../models/digitaal-adres-config';
import {EMPTY, of} from 'rxjs';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {PluginService} from '@valtimo/plugin';

describe('UpdateDigitaalAdresComponent', () => {
    let component: UpdateDigitaalAdresComponent;
    let fixture: ComponentFixture<UpdateDigitaalAdresComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                UpdateDigitaalAdresComponent,
                TranslateModule.forRoot({
                    loader: {provide: TranslateLoader, useClass: TranslateFakeLoader}
                })
            ],
            providers: [{provide: PluginService, useValue: {} as Partial<PluginService>}]
        }).compileComponents();

        fixture = TestBed.createComponent(UpdateDigitaalAdresComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(UpdateDigitaalAdresComponent);
        component = fixture.componentInstance;

        component.save$ = EMPTY;
        component.disabled$ = of(false);
        component.pluginId = 'test-plugin';

        component.prefillConfiguration$ = of({
            resultPvName: '',
            verstrektDoorBetrokkene: '',
            verstrektDoorPartij: '',
            adres: '',
            soortDigitaalAdres: '',
            isStandaardAdres: '',
            omschrijving: '',
            referentie: '',
            verificatieDatum: ''
        } as DigitaalAdresConfig);

        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
