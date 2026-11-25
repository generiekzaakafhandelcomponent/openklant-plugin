import {ComponentFixture, TestBed} from '@angular/core/testing';
import {CreateDigitaalAdresComponent} from './create-digitaal-adres.component';
import {EMPTY, of} from 'rxjs';
import {DigitaalAdresConfig} from '../../models/digitaal-adres-config';

describe('CreateDigitaalAdresComponent', () => {
    let component: CreateDigitaalAdresComponent;
    let fixture: ComponentFixture<CreateDigitaalAdresComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CreateDigitaalAdresComponent]
        })
            .overrideTemplate(CreateDigitaalAdresComponent, '')
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(CreateDigitaalAdresComponent);
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

    const base = {resultPvName: 'adres', adres: 'jan@example.com', soortDigitaalAdres: 'email'};

    function validityOf(config: DigitaalAdresConfig): boolean[] {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));
        component.formValueChange(config);
        return emitted;
    }

    it('should accept an adres provided only by a betrokkene', () => {
        expect(validityOf({...base, verstrektDoorBetrokkene: 'pv:betrokkeneUuid'})).toEqual([true]);
    });

    it('should accept an adres provided only by a partij', () => {
        expect(validityOf({...base, verstrektDoorPartij: 'pv:partijUuid'})).toEqual([true]);
    });

    it('should reject an adres without partij and betrokkene', () => {
        expect(validityOf({...base})).toEqual([false]);
    });
});
