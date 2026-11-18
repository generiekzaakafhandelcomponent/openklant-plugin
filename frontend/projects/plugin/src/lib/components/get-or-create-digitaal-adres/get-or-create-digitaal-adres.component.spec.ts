import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {GetOrCreateDigitaalAdresComponent} from './get-or-create-digitaal-adres.component';
import {GetOrCreateDigitaalAdresConfig} from '../../models/get-or-create-digitaal-adres-config';

describe('GetOrCreateDigitaalAdresComponent', () => {
    let component: GetOrCreateDigitaalAdresComponent;
    let fixture: ComponentFixture<GetOrCreateDigitaalAdresComponent>;
    let save$: Subject<void>;

    const base: GetOrCreateDigitaalAdresConfig = {adres: 'pv:email', soortDigitaalAdres: 'email', resultPvName: 'emailUuid'};

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [GetOrCreateDigitaalAdresComponent]
        })
            .overrideTemplate(GetOrCreateDigitaalAdresComponent, '')
            .compileComponents();

        save$ = new Subject<void>();
        fixture = TestBed.createComponent(GetOrCreateDigitaalAdresComponent);
        component = fixture.componentInstance;
        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(base);
        fixture.detectChanges();
    });

    afterEach(() => save$.complete());

    function validityOf(config: GetOrCreateDigitaalAdresConfig): boolean[] {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));
        component.formValueChange(config);
        return emitted;
    }

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should accept an adres of a partij', () => {
        expect(validityOf({...base, verstrektDoorPartij: 'pv:partijUuid'})).toEqual([true]);
    });

    it('should accept an adres of only a betrokkene', () => {
        expect(validityOf({...base, verstrektDoorBetrokkene: 'pv:betrokkeneUuid'})).toEqual([true]);
    });

    it('should reject a configuration without partij and betrokkene', () => {
        expect(validityOf(base)).toEqual([false]);
    });

    it('should emit the configuration on save when it is valid', () => {
        const config = {...base, verstrektDoorPartij: 'pv:partijUuid', createdPvName: 'emailAangemaakt'};
        const emitted: unknown[] = [];
        component.configuration.subscribe(c => emitted.push(c));

        component.formValueChange(config);
        save$.next();

        expect(emitted).toEqual([config]);
    });
});
